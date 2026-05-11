package com.tech.motjip.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.kakao.sdk.user.UserApiClient;
import com.tech.motjip.API.RetrofitClient;
import com.tech.motjip.Auth.TokenManager;
import com.tech.motjip.Dto.RequestDto.KakaoSdkLoginRequestDto;
import com.tech.motjip.Dto.ResponseDto.LoginResponseDto;

import kotlin.Unit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainController {

    private static final String TAG = "MainControllerDebug";

    private final Activity activity;
    private final Context context;
    private final TokenManager tokenManager;
    private final MainControllerCallback callback;

    // 💡 인터페이스에 onLoginFail 추가
    public interface MainControllerCallback {
        void onNeedLoginScreen();
        void onLoginSuccess(LoginResponseDto user);
        void onNeedFinish();
        void onLoginFail(String message); // 이 줄이 추가되어야 MainActivity 에러가 사라집니다.
    }

    public MainController(Activity activity, MainControllerCallback callback) {
        this.activity = activity;
        this.context = activity;
        this.callback = callback;
        this.tokenManager = new TokenManager(context);
    }

    public boolean handleDeepLinkIfNeeded(Intent intent) {
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Log.d(TAG, "딥링크 감지");
            handleIntent(intent);
            return true;
        }
        return false;
    }

    public void checkAutoLogin() {
        if (!tokenManager.hasAccessToken()) {
            callback.onNeedLoginScreen();
            return;
        }

        Log.d(TAG, "AccessToken 있음 → 자동 로그인");

        RetrofitClient.getApiService(context)
                .getCurrentUser()
                .enqueue(new Callback<LoginResponseDto>() {
                    @Override
                    public void onResponse(Call<LoginResponseDto> call, Response<LoginResponseDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onLoginSuccess(response.body());
                            callback.onNeedFinish();
                        } else {
                            tokenManager.clearTokens();
                            callback.onNeedLoginScreen();
                            // 자동 로그인 실패는 사용자 방해를 피하기 위해 모달을 띄우지 않는 것이 자연스럽습니다.
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponseDto> call, Throwable t) {
                        Log.e(TAG, "API 실패: " + t.getMessage());
                        callback.onNeedLoginScreen();
                    }
                });
    }

    public void loginWithKakaoSdk() {
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(context)) {
            Log.d(TAG, "카카오톡 앱 로그인 가능");
            UserApiClient.getInstance().loginWithKakaoTalk(context, (token, error) -> {
                if (error != null) {
                    Log.e(TAG, "카카오톡 로그인 실패", error);
                    loginWithKakaoAccount(); // 실패 시 웹 로그인 시도
                    return Unit.INSTANCE;
                }
                if (token != null) {
                    handleKakaoAccessToken(token.getAccessToken());
                }
                return Unit.INSTANCE;
            });
        } else {
            loginWithKakaoAccount();
        }
    }

    private void loginWithKakaoAccount() {
        UserApiClient.getInstance().loginWithKakaoAccount(context, (token, error) -> {
            if (error != null) {
                Log.e(TAG, "카카오계정 로그인 실패", error);
                // 💡 사용자가 직접 시도한 로그인이 실패했을 때 모달 호출
                callback.onLoginFail("카카오 로그인에 실패했습니다.");
                return Unit.INSTANCE;
            }
            if (token != null) {
                handleKakaoAccessToken(token.getAccessToken());
            }
            return Unit.INSTANCE;
        });
    }

    private void handleKakaoAccessToken(String kakaoAccessToken) {
        RetrofitClient.getApiService(context)
                .loginWithKakaoSdk(new KakaoSdkLoginRequestDto(kakaoAccessToken))
                .enqueue(new Callback<LoginResponseDto>() {
                    @Override
                    public void onResponse(Call<LoginResponseDto> call, Response<LoginResponseDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponseDto loginResponse = response.body();
                            tokenManager.saveTokens(loginResponse.getAccessToken(), loginResponse.getRefreshToken());
                            callback.onLoginSuccess(loginResponse);
                            callback.onNeedFinish();
                        } else {
                            // 💡 서버 응답 에러 시 모달 호출
                            callback.onLoginFail("로그인 인증에 실패했습니다.");
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponseDto> call, Throwable t) {
                        // 💡 네트워크 통신 실패 시 모달 호출
                        callback.onLoginFail("네트워크 서버 연결에 실패했습니다.");
                    }
                });
    }

    private void handleIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri == null) {
            callback.onNeedLoginScreen();
            return;
        }

        String accessToken = uri.getQueryParameter("access_token");
        String refreshToken = uri.getQueryParameter("refresh_token");

        if (accessToken == null || refreshToken == null) {
            tokenManager.clearTokens();
            callback.onNeedLoginScreen();
            return;
        }

        tokenManager.saveTokens(accessToken, refreshToken);

        RetrofitClient.getApiService(context)
                .getCurrentUser()
                .enqueue(new Callback<LoginResponseDto>() {
                    @Override
                    public void onResponse(Call<LoginResponseDto> call, Response<LoginResponseDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onLoginSuccess(response.body());
                            callback.onNeedFinish();
                        } else {
                            tokenManager.clearTokens();
                            callback.onNeedLoginScreen();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponseDto> call, Throwable t) {
                        tokenManager.clearTokens();
                        callback.onNeedLoginScreen();
                    }
                });
    }
}