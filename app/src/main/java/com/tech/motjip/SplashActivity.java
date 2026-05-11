package com.tech.motjip;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        VideoView videoView = findViewById(R.id.videoView);

        Uri videoUri = Uri.parse(
                "android.resource://" + getPackageName() + "/" + R.raw.bowl1
        );

        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(mp -> {

            // 화면을 꽉 채움
            // 단, 기기 비율과 영상 비율이 다르면 가장자리 일부는 잘릴 수 있음
            mp.setVideoScalingMode(
                    MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            );

            videoView.start();
        });

        videoView.setOnCompletionListener(mediaPlayer -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        });
    }
}