package com.tech.motjip.Controller;

import android.app.Activity;
import android.util.Log;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapView;
import com.tech.motjip.API.KakaoMap.CallbackInterface.IMapStartCallback;
import com.tech.motjip.API.KakaoMap.KakaoMapHandler;
import com.tech.motjip.API.KakaoMap.KakaoMapStarter;
import com.tech.motjip.API.KakaoMap.Utils.MapHelper;
import com.tech.motjip.Model.KeywordMapVO;
import com.tech.motjip.Model.MapPostionVO;
import com.tech.motjip.Thread.IThreadCallback;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class TestController implements IMapStartCallback{

    private final Activity testActivity;

    private KakaoMapStarter mapStarter;

    private KakaoMapHandler mapHandler;

    // 비동기 멀티 쓰레드 작업이 완료되었음을 알립니다.
    IThreadCallback callback;

    @Inject
    TestController(Activity testActivity)
    {
        this.testActivity = testActivity;
    }

    // 콜백함수를 할당하고 맵뷰에 맵을 그립니다.
    public void mapStart(MapView mapView, IThreadCallback callback)
    {
        this.mapStarter = new KakaoMapStarter(mapView, this, testActivity);
        this.callback = callback;
        mapStarter.start();
    }

    // 마커를 그립니다(테스트용)
    public void drawMarker()
    {
        mapHandler.setMarker(MapHelper.getLatLng(new MapPostionVO(37.53660174890449, 126.8819899200535)), "테스트 입니다");
    }

    // 검색 결과값에 따른 마커를 그립니다.
    public void drawMarker(List<KeywordMapVO> vo)
    {
        for(KeywordMapVO voList : vo){
            LatLng postion = MapHelper.getLatLng(voList.getX(),voList.getY());
            mapHandler.setMarker(postion,voList.getPlace_name());
        }
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onReady(KakaoMap kakaoMap) {
        this.mapHandler = new KakaoMapHandler(kakaoMap);
        Log.e("KakaoMap", "맵 로드 성공");
        //drawMarker();
        callback.ThreadEnds();
    }
}
