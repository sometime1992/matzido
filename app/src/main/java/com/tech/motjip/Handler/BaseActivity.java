package com.tech.motjip.Handler;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

/**
 * 각 액티비티에서 공통된 로직을 사용하기위한 추상 베이스클래스
 */
public abstract class BaseActivity extends AppCompatActivity {

    // 각 액티비티 추상클래스 상속 필수
    @Inject
    protected BackKeyHandler backKeyHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 뒤로가기키 처리
        backKeyHandler.handleBackkey(this);
    }
}
