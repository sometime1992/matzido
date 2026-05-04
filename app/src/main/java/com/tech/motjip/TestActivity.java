package com.tech.motjip;

import android.os.Bundle;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapView;
import com.tech.motjip.API.HttpHelper.GetJsonAsync;
import com.tech.motjip.API.KakaoMap.Utils.MapHelper;
import com.tech.motjip.Controller.TestController;
import com.tech.motjip.Handler.BaseActivity;
import com.tech.motjip.Model.KeywordMapVO;
import com.tech.motjip.Thread.IThreadCallback;
import com.tech.motjip.Thread.IThreadReturn1Callback;

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
                IThreadReturn1Callback<List<KeywordMapVO>> result = new IThreadReturn1Callback<List<KeywordMapVO>>() {
                    @Override
                    public void ThreadEnds(List<KeywordMapVO> result) {
                        controller.drawMarker(result);
                        LatLng position = MapHelper.getLatLng(result.get(0).getX(),result.get(0).getY());
                        controller.moveMapCamara(position);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                };

                GetJsonAsync.GetMapSearchDataWithConditionsAsync("햄버거", "129.0192326360133","35.217951030549614","2000", result);
            }
        };

        controller.mapStart(mapView, callback);
    }



    // 추가로 지도 리사이클 핸들링 필요
    // https://apis.map.kakao.com/android_v2/docs/getting-started/quickstart/#3-지도-시작-및-kakaomap-객체-가져오기
}