package com.tech.motjip;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kakao.sdk.common.KakaoSdk;
import com.tech.motjip.BuildConfig;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MotJipApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 카카오 SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY);

        // 앱 로그 관리용
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(
                    @NonNull Activity activity,
                    @Nullable Bundle savedInstanceState
            ) {
                Log.d("액티비티", "생성됨 : " + activity.getLocalClassName());
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(
                    @NonNull Activity activity,
                    @NonNull Bundle outState
            ) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Log.d("액티비티", "종료됨 : " + activity.getLocalClassName());
            }
        });
    }
}