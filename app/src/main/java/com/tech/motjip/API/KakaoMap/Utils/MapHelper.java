package com.tech.motjip.API.KakaoMap.Utils;

import com.kakao.vectormap.LatLng;
import com.tech.motjip.Model.MapPostionVO;

// 카카오 맵에서 사용될 데이터 변환 등의 헬퍼 클래스입니다.
public class MapHelper {

    // 맵 좌표에 대한 LatLng객체를 반환합니다.
    public static LatLng getLatLng(MapPostionVO vo){
        return LatLng.from(vo.getLatY(),vo.getLngX());
    }

    // @오버로드, 맵 좌표에 대한 LatLng객체를 반환합니다.
    public static LatLng getLatLng(String x, String y){
        return LatLng.from(Double.parseDouble(y), Double.parseDouble(x));
    }

}
