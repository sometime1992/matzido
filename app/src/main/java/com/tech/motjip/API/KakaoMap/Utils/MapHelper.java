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

    // 좌표가 한국 범위 내에 있는지 확인합니다.
    public static boolean isInKorea(double lat, double lng) {
        return lat >= 33.0 && lat <= 38.9 && lng >= 124.5 && lng <= 131.9;
    }

}
