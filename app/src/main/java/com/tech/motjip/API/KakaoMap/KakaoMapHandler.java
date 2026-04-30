package com.tech.motjip.API.KakaoMap;

import android.graphics.Color;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;
import com.kakao.vectormap.label.LabelTextBuilder;
import com.tech.motjip.R;

import javax.annotation.Nonnull;

import lombok.NonNull;

public class KakaoMapHandler {
    private final KakaoMap kakaoMap;
    private LabelStyles labelStyle;
    public KakaoMapHandler(KakaoMap kakaomap){
        this.kakaoMap = kakaomap;
        this.labelStyle = getDefaultLabelStyle();
    }

    // 카메라를 이동시킵니다.
    // https://apis.map.kakao.com/android_v2/docs/getting-started/precautions/#2-api-의-비동기-처리
    public void moveCamera(@NonNull LatLng position){
        kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(position));
    }

    // 맵에 마커를 찍습니다.
    // https://apis.map.kakao.com/android_v2/docs/api-guide/label/label/#1-label-생성하기
    public void setMarker(@Nonnull LatLng position, String labelText){
        LabelOptions options = LabelOptions.from(position)
                .setStyles(labelStyle);

        LabelLayer layer = kakaoMap.getLabelManager().getLayer();

        Label label = layer.addLabel(options);

        // https://apis.map.kakao.com/android_v2/docs/api-guide/label/label/#스타일-및-텍스트-변경
        // 이부분 api문서에없음...LabelTextBuilder 클래스에 대한 확인 필요
        label.changeText(new LabelTextBuilder().setTexts(labelText));
        // 주변 마커들 숨김 마커가 주변의 지도 그림위에 올라가있음 마커 주변으로 폴리곤을 그려서 지도를 덮어쓰든지 아예 다없애든지...
        // kakaoMap.setPoiVisible(false);
    }

    // 기본 마커 스타일을 가져옵니다.
    private LabelStyles getDefaultLabelStyle()
    {
        // 마커 이미지 할당 및 텍스트 크기 및 글자 색 적용 이미지 크기 나중에 조정필요
        return kakaoMap.getLabelManager()
                .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.loca_icon).setTextStyles(20, Color.BLACK)));
    }
}
