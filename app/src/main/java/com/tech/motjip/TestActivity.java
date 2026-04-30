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
import com.tech.motjip.API.HttpHelper.GetJson;
import com.tech.motjip.API.KakaoMap.CallbackInterface.IMapStartCallback;
import com.tech.motjip.API.KakaoMap.KakaoMapHandler;
import com.tech.motjip.API.KakaoMap.KakaoMapStarter;
import com.tech.motjip.Controller.TestController;
import com.tech.motjip.Handler.BaseActivity;
import com.tech.motjip.Model.KeywordMapVO;
import com.tech.motjip.Thread.IThreadCallback;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TestActivity extends BaseActivity{

    @Inject
    TestController controller;

    IThreadCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        MapView mapView = findViewById(R.id.map_view);

        // 테스트용
        callback = new IThreadCallback() {
            @Override
            public void ThreadEnds() {
                // 통신 멀티쓰레딩 처리
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<KeywordMapVO> resultList = GetJson.GetMapSearchDataWithConditions("피자", "126.8819899200535", "37.53660174890449", "2000");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    controller.drawMarker(resultList);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };

        controller.mapStart(mapView, callback);
    }

    // 추가로 지도 리사이클 핸들링 필요
    // https://apis.map.kakao.com/android_v2/docs/getting-started/quickstart/#3-지도-시작-및-kakaomap-객체-가져오기
}