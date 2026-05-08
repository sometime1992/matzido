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

    public interface MainControllerCallback {

        void onNeedLoginScreen();

        void onLoginSuccess(LoginResponseDto user);

        void onNeedFinish();
    }

    public MainController(
            Activity activity,
            MainControllerCallback callback
    ) {
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
                    public void onResponse(
                            Call<LoginResponseDto> call,
                            Response<LoginResponseDto> response
                    ) {

                        Log.d(TAG, "자동 로그인 응답: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {

                            callback.onLoginSuccess(response.body());

                            callback.onNeedFinish();

                        } else {

                            tokenManager.clearTokens();

                            callback.onNeedLoginScreen();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<LoginResponseDto> call,
                            Throwable t
                    ) {

                        Log.e(TAG, "API 실패: " + t.getMessage());

                        callback.onNeedLoginScreen();
                    }
                });
    }

    public void loginWithKakaoSdk() {

        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(context)) {

            Log.d(TAG, "카카오톡 앱 로그인 가능 → 카카오톡 로그인 시도");

            UserApiClient.getInstance().loginWithKakaoTalk(
                    context,
                    (token, error) -> {

                        if (error != null) {

                            Log.e(
                                    TAG,
                                    "카카오톡 로그인 실패 → 카카오계정 웹 로그인 시도",
                                    error
                            );

                            loginWithKakaoAccount();

                            return Unit.INSTANCE;
                        }

                        if (token != null) {

                            Log.d(TAG, "카카오톡 로그인 성공");

                            handleKakaoAccessToken(token.getAccessToken());
                        }

                        return Unit.INSTANCE;
                    }
            );

        } else {

            Log.d(TAG, "카카오톡 미설치 → 카카오계정 웹 로그인 시도");

            loginWithKakaoAccount();
        }
    }

    private void loginWithKakaoAccount() {

        UserApiClient.getInstance().loginWithKakaoAccount(
                context,
                (token, error) -> {

                    if (error != null) {

                        Log.e(TAG, "카카오계정 로그인 실패", error);

                        callback.onNeedLoginScreen();

                        return Unit.INSTANCE;
                    }

                    if (token != null) {

                        Log.d(TAG, "카카오계정 로그인 성공");

                        handleKakaoAccessToken(token.getAccessToken());
                    }

                    return Unit.INSTANCE;
                }
        );
    }

    private void handleKakaoAccessToken(String kakaoAccessToken) {

        Log.d(TAG, "카카오 AccessToken 획득");

        RetrofitClient.getApiService(context)
                .loginWithKakaoSdk(
                        new KakaoSdkLoginRequestDto(kakaoAccessToken)
                )
                .enqueue(new Callback<LoginResponseDto>() {

                    @Override
                    public void onResponse(
                            Call<LoginResponseDto> call,
                            Response<LoginResponseDto> response
                    ) {

                        Log.d(
                                TAG,
                                "카카오 SDK 백엔드 로그인 응답: " + response.code()
                        );

                        if (response.isSuccessful() && response.body() != null) {

                            LoginResponseDto loginResponse = response.body();

                            tokenManager.saveTokens(
                                    loginResponse.getAccessToken(),
                                    loginResponse.getRefreshToken()
                            );

                            callback.onLoginSuccess(loginResponse);

                            callback.onNeedFinish();

                        } else {

                            Log.e(
                                    TAG,
                                    "카카오 SDK 백엔드 로그인 실패: "
                                            + response.code()
                            );

                            callback.onNeedLoginScreen();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<LoginResponseDto> call,
                            Throwable t
                    ) {

                        Log.e(
                                TAG,
                                "카카오 SDK 백엔드 통신 실패: "
                                        + t.getMessage()
                        );

                        callback.onNeedLoginScreen();
                    }
                });
    }

    private void handleIntent(Intent intent) {

        Uri uri = intent.getData();

        if (uri == null) {

            callback.onNeedLoginScreen();

            return;
        }

        Log.d(TAG, "딥링크 URI: " + uri);

        String accessToken = uri.getQueryParameter("access_token");
        String refreshToken = uri.getQueryParameter("refresh_token");

        if (accessToken == null || refreshToken == null) {

            Log.e(TAG, "토큰 없음 → 로그인 화면 이동");

            tokenManager.clearTokens();

            callback.onNeedLoginScreen();

            return;
        }

        Log.d(TAG, "AccessToken 저장");

        tokenManager.saveTokens(accessToken, refreshToken);

        RetrofitClient.getApiService(context)
                .getCurrentUser()
                .enqueue(new Callback<LoginResponseDto>() {

                    @Override
                    public void onResponse(
                            Call<LoginResponseDto> call,
                            Response<LoginResponseDto> response
                    ) {

                        Log.d(
                                TAG,
                                "딥링크 유저 조회 응답: " + response.code()
                        );

                        if (response.isSuccessful() && response.body() != null) {

                            callback.onLoginSuccess(response.body());

                            callback.onNeedFinish();

                        } else {

                            tokenManager.clearTokens();

                            callback.onNeedLoginScreen();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<LoginResponseDto> call,
                            Throwable t
                    ) {

                        Log.e(
                                TAG,
                                "네트워크 실패: " + t.getMessage()
                        );

                        tokenManager.clearTokens();

                        callback.onNeedLoginScreen();
                    }
                });
    }
}