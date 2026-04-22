package com.tech.motjip;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.tech.motjip.Controller.TestController;
import com.tech.motjip.Handler.BaseActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TestActivity extends BaseActivity {

    @Inject
    TestController controller;

    KakaoMap kakaoMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        KakaoMapSdk.init(this, "bd102e084259f6700a25cc16ce61acda");
        MapView mapView = findViewById(R.id.map_view);
        Log.d("test",KakaoMapSdk.INSTANCE.getAppKey());
        Log.d("test",KakaoMapSdk.INSTANCE.getHashKey());

        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {
                Log.d("KakaoMap", "onMapDestroy: ");
            }

            @Override
            public void onMapError(Exception e) {
                Log.e("KakaoMap", "onMapError: ", e);
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull KakaoMap map) {
                kakaoMap = map;
            }
        });
    }
}