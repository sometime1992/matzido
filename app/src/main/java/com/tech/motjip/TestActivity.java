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
import com.tech.motjip.API.KakaoMap.CallbackInterface.IMapStartCallback;
import com.tech.motjip.API.KakaoMap.KakaoMapStarter;
import com.tech.motjip.Controller.TestController;
import com.tech.motjip.Handler.BaseActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TestActivity extends BaseActivity implements IMapStartCallback {

    @Inject
    TestController controller;

    KakaoMap kakaoMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        MapView mapView = findViewById(R.id.map_view);

        KakaoMapStarter starter = new KakaoMapStarter(mapView, this, this);

        starter.start();

//        KakaoMapSdk.init(this, "c2f31aa92db76b6d090b77365cf633bd");
//        Log.d("test",KakaoMapSdk.INSTANCE.getAppKey());
//        Log.d("test",KakaoMapSdk.INSTANCE.getHashKey());
//
//        mapView.start(new MapLifeCycleCallback() {
//            @Override
//            public void onMapDestroy() {
//                Log.d("KakaoMap", "onMapDestroy: ");
//            }
//
//            @Override
//            public void onMapError(Exception e) {
//                Log.e("KakaoMap", "onMapError: ", e);
//            }
//        }, new KakaoMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull KakaoMap map) {
//                kakaoMap = map;
//                Log.e("KakaoMap", "성공");
//            }
//        });
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onReady(KakaoMap kakaoMap) {
        this.kakaoMap = kakaoMap;
        Log.e("KakaoMap", "성공");
    }
}