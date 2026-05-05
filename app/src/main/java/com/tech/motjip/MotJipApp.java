package com.tech.motjip;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kakao.sdk.common.KakaoSdk; // 👈 카카오 라이브러리 추가

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MotJipApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 🚀 1. 카카오 SDK 초기화 (이게 없어서 튕겼던 겁니다!)
        // 기철님의 네이티브 앱 키를 여기에 정확히 넣었습니다.
        KakaoSdk.init(this, "fc96e5701f247062fd7a0af17ce8e526");

        // 2. 기존 액티비티 로그 관리 로직 (그대로 유지)
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                Log.d("액티비티", "생성됨 : " + activity.getLocalClassName());
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {}

            @Override
            public void onActivityResumed(@NonNull Activity activity) {}

            @Override
            public void onActivityPaused(@NonNull Activity activity) {}

            @Override
            public void onActivityStopped(@NonNull Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Log.d("액티비티", "종료됨 : " + activity.getLocalClassName());
            }
        });
    }
}