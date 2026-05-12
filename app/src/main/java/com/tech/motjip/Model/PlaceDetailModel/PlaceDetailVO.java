package com.tech.motjip.Model.PlaceDetailModel;

import java.util.List;

import lombok.Data;

@Data
// 카카오 장소 상세 API (panel3) 응답을 단순화한 도메인 모델
public class PlaceDetailVO {

    // summary.phone_numbers[0].tel
    private String phoneNumber;

    // menu.menus.items[]
    private List<MenuVO> menus;

    // open_hours.headline.code (예: "OPEN")
    private String openStatus;
    // open_hours.headline.display_text (예: "영업 중")
    private String openDisplay;
    // open_hours.headline.display_text_info (예: "22:00 까지")
    private String openTimeText;

    // open_hours.week_from_today.week_periods[].days[] 를 평탄화
    private List<OpenTimeVO> openTimes;

    // photos.photos[].url 만 추출
    private List<String> photoUrls;

    // kakaomap_review.reviews[]
    private List<ReviewVO> reviews;
}
