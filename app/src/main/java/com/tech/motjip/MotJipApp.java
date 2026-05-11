package com.tech.motjip;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kakao.sdk.common.KakaoSdk;
import com.tech.motjip.API.RetrofitClient;
import com.tech.motjip.BuildConfig;
import com.tech.motjip.Dto.RequestDto.StatusUpdateRequestDto;
import com.tech.motjip.Utils.LoginStateManager;

import dagger.hilt.android.HiltAndroidApp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltAndroidApp
public class MotJipApp extends Application {

    private static final String PREF_NAME = "AppPrefs";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    // 현재 실행중인 Activity 개수
    private int startedActivityCount = 0;

    @Override
    public void onCreate() {

        super.onCreate();

        // 카카오 SDK 초기화
        KakaoSdk.init(
                this,
                BuildConfig.KAKAO_NATIVE_APP_KEY
        );

        // 앱 로그 관리용
        registerActivityLifecycleCallbacks(
                new ActivityLifecycleCallbacks() {

                    @Override
                    public void onActivityCreated(
                            @NonNull Activity activity,
                            @Nullable Bundle savedInstanceState
                    ) {

                        Log.d(
                                "액티비티",
                                "생성됨 : "
                                        + activity.getLocalClassName()
                        );
                    }

                    @Override
                    public void onActivityStarted(
                            @NonNull Activity activity
                    ) {

                        startedActivityCount++;

                        // 앱이 foreground로 진입한 최초 순간만 처리
                        if (startedActivityCount == 1) {

                            Log.d(
                                    "앱상태",
                                    "Foreground 진입"
                            );

                            // 토큰 있을 때만 상태 변경
                            if (hasAccessToken()) {

                                LoginStateManager.setLoginStatus(
                                        MotJipApp.this,
                                        LoginStateManager.LOGIN
                                );

                                updateServerStatus(1);

                            } else {

                                Log.d(
                                        "상태변경",
                                        "토큰 없음 → Foreground 상태 변경 생략"
                                );
                            }
                        }
                    }

                    @Override
                    public void onActivityResumed(
                            @NonNull Activity activity
                    ) {

                    }

                    @Override
                    public void onActivityPaused(
                            @NonNull Activity activity
                    ) {

                    }

                    @Override
                    public void onActivityStopped(
                            @NonNull Activity activity
                    ) {

                        startedActivityCount--;

                        // 앱 전체가 background로 이동
                        if (startedActivityCount == 0) {

                            Log.d(
                                    "앱상태",
                                    "Background 이동"
                            );

                            // 토큰 있을 때만 상태 변경
                            if (hasAccessToken()) {

                                LoginStateManager.setLoginStatus(
                                        MotJipApp.this,
                                        LoginStateManager.LOGOUT
                                );

                                updateServerStatus(0);

                            } else {

                                Log.d(
                                        "상태변경",
                                        "토큰 없음 → Background 상태 변경 생략"
                                );
                            }
                        }
                    }

                    @Override
                    public void onActivitySaveInstanceState(
                            @NonNull Activity activity,
                            @NonNull Bundle outState
                    ) {

                    }

                    @Override
                    public void onActivityDestroyed(
                            @NonNull Activity activity
                    ) {

                        Log.d(
                                "액티비티",
                                "종료됨 : "
                                        + activity.getLocalClassName()
                        );
                    }
                }
        );
    }

    private boolean hasAccessToken() {

        SharedPreferences preferences =
                getSharedPreferences(
                        PREF_NAME,
                        MODE_PRIVATE
                );

        String accessToken =
                preferences.getString(
                        ACCESS_TOKEN,
                        null
                );

        return accessToken != null
                && !accessToken.trim().isEmpty();
    }

    private void updateServerStatus(
            int statusCode
    ) {

        // 토큰 없으면 요청 안 보냄
        if (!hasAccessToken()) {

            Log.d(
                    "상태변경",
                    "토큰 없음 → 서버 상태 변경 요청 생략"
            );

            return;
        }

        RetrofitClient.getApiService(this)
                .updateMyStatus(
                        new StatusUpdateRequestDto(statusCode)
                )
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {

                        Log.d(
                                "상태변경",
                                "서버 상태 변경 성공 : "
                                        + statusCode
                        );
                    }

                    @Override
                    public void onFailure(
                            Call<Void> call,
                            Throwable t
                    ) {

                        Log.e(
                                "상태변경",
                                "서버 상태 변경 실패",
                                t
                        );
                    }
                });
    }
}