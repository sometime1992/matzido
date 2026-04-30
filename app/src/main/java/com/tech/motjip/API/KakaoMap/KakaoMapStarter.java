package com.tech.motjip.API.KakaoMap;

import android.app.Activity;

import androidx.lifecycle.DefaultLifecycleObserver;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.tech.motjip.API.KakaoMap.CallbackInterface.IMapStartCallback;
import com.tech.motjip.API.KakaoMap.Utils.MapHelper;
import com.tech.motjip.Model.MapPostionVO;

import lombok.NonNull;

// 카카오맵을 초기화하고 뷰에 표시합니다.(맵객체 라이프사이클 제어를 위해 DefaultLifecycleObserver로 액티비티의 옵저버로 연결할필요있음)
public class KakaoMapStarter extends MapLifeCycleCallback implements DefaultLifecycleObserver {

    // 콜백함수의 객체입니다.
    private final IMapStartCallback callback;
    // 카카오맵을 표시할 맵뷰입니다.
    private final MapView mapView;
    // 카카오맵에 사용될 네이티브 앱키 입니다.
    private final String apiKey = "c2f31aa92db76b6d090b77365cf633bd";

    public KakaoMapStarter(@NonNull MapView mapView, @NonNull IMapStartCallback callback, @NonNull Activity activity){
        this.mapView = mapView;
        this.callback = callback;
        KakaoMapSdk.init(activity, apiKey);
    }

    // 카카오맵을 시작하고 뷰에 표현합니다.
    public void start(){
        mapView.start(this, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull KakaoMap kakaoMap) {
                // 준비가 완료되면 카카오맵 객체를 반환합니다
                callback.onReady(kakaoMap);
            }

            // 맵 로드시 시작 좌표
            @Override
            @NonNull
            public LatLng getPosition() {
                return getPostion();
            }
        });
    }

    // 카카오맵 API의 네이티브 앱키를 가져옵니다.
    public String getAppKey()
    {
        return KakaoMapSdk.INSTANCE.getAppKey();
    }

    // 안드로이드 앱의 해쉬키를 가져옵니다.
    public String getHashKey()
    {
        return KakaoMapSdk.INSTANCE.getHashKey();
    }

    // 현재 사용자의 위치를 반환합니다.
    private LatLng getPostion(){
        // 임시로 피자헛 목동점 좌표 넣어둠 나중에 내 현재 GPS 위치 가져오는 함수로 바꿈
        MapPostionVO vo = new MapPostionVO(37.53660174890449, 126.8819899200535);
        return MapHelper.getLatLng(vo);
    }

    @Override
    public void onMapDestroy() {
        // 맵 객체 꺼졌을떄 처리
    }

    // 맵 에러 발생시 콜백함수에 에러 반환
    @Override
    public void onMapError(Exception e) {
        callback.onError(e);
    }
}
