package com.tech.motjip;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 레이아웃 설정
        setContentView(R.layout.activity_splash);

        VideoView videoView = findViewById(R.id.videoView);

        // res/raw 폴더에 넣은 영상 파일 경로 설정 (예: res/raw/bowl1.mp4)
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bowl1);
        videoView.setVideoURI(videoUri);

        // 영상 재생 완료 시 메인 화면으로 이동
        videoView.setOnCompletionListener(mediaPlayer -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish(); // SplashActivity 종료
        });

        // 영상 시작
        videoView.start();
    }
}