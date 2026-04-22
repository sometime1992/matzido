package com.tech.motjip.Handler;

import android.app.Activity;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;


/**
 * 뒤로가기 버튼 제어 클래스
 */
public class BackKeyHandler {

    // 뒤로가기 버튼이 클릭된 시간 저장 전역변수
    private long lastTimeBackPressed;

    // 뒤로가기 누를 수 있는 시간
    private final int backkeyPressDelay = 2000;

    @Inject
    public BackKeyHandler() {

    }

    // 뒤로가기키를 제어하기위한 함수
    public void handleBackkey(final Activity activity){
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (System.currentTimeMillis() - lastTimeBackPressed <= backkeyPressDelay) {
                    AppHandler.off(activity);
                } else {
                    Toast.makeText(activity, "'뒤로' 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
                    // 버튼 클릭된 시간 기록
                    lastTimeBackPressed = System.currentTimeMillis();
                }
            }
        };

        ((AppCompatActivity) activity).getOnBackPressedDispatcher().addCallback((AppCompatActivity) activity, callback);
    }
}
