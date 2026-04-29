package com.tech.motjip.API.KakaoMap.CallbackInterface;

import com.kakao.vectormap.KakaoMap;

// 카카오 맵 처리시 사용될 콜백함수를 정의해둔 인터페이스 입니다.
public interface IMapStartCallback {
    // 에러가 반환될시 처리할 함수
    public abstract void onError(Exception e);
    // 카카오맵을 사용할 준비가 되었을떄 카카오맵 객체 반환
    public abstract void onReady(KakaoMap kakaoMap);
}
