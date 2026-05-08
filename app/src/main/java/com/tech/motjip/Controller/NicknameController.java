package com.tech.motjip.Controller;

import android.content.Context;
import android.util.Log;

import com.tech.motjip.API.RetrofitClient;
import com.tech.motjip.Auth.TokenManager;
import com.tech.motjip.Dto.RequestDto.NicknameUpdateRequestDto;
import com.tech.motjip.Dto.ResponseDto.LoginResponseDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NicknameController {

    private static final String TAG = "NicknameControllerDebug";

    private final Context context;
    private final TokenManager tokenManager;
    private final NicknameControllerCallback callback;

    public interface NicknameControllerCallback {

        void onNicknameSuccess(LoginResponseDto user);

        void onNicknameFail(String message);
    }

    public NicknameController(
            Context context,
            NicknameControllerCallback callback
    ) {
        this.context = context;
        this.callback = callback;
        this.tokenManager = new TokenManager(context);
    }

    public void updateNickname(
            Long memberId,
            String nicknameToSend
    ) {

        if (memberId == null || memberId == -1L) {

            callback.onNicknameFail("로그인이 필요합니다.");

            return;
        }

        NicknameUpdateRequestDto requestDto =
                new NicknameUpdateRequestDto(
                        memberId,
                        nicknameToSend
                );

        Log.d(TAG, "닉네임 요청 시작");

        RetrofitClient.getApiService(context)
                .updateNickname(requestDto)
                .enqueue(new Callback<LoginResponseDto>() {

                    @Override
                    public void onResponse(
                            Call<LoginResponseDto> call,
                            Response<LoginResponseDto> response
                    ) {

                        Log.d(TAG, "응답 코드: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {

                            LoginResponseDto data = response.body();

                            Log.d(
                                    TAG,
                                    "새 AccessToken: " + data.getAccessToken()
                            );

                            Log.d(
                                    TAG,
                                    "새 RefreshToken: " + data.getRefreshToken()
                            );

                            tokenManager.saveTokens(
                                    data.getAccessToken(),
                                    data.getRefreshToken()
                            );

                            callback.onNicknameSuccess(data);

                        } else {

                            Log.e(
                                    TAG,
                                    "닉네임 설정 실패: " + response.code()
                            );

                            callback.onNicknameFail("닉네임 설정 실패");
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<LoginResponseDto> call,
                            Throwable t
                    ) {

                        Log.e(TAG, "서버 연결 실패: " + t.getMessage());

                        callback.onNicknameFail("서버 연결 실패");
                    }
                });
    }
}