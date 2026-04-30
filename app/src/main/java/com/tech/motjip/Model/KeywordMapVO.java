package com.tech.motjip.Model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
// 키워드 검색에 대한 RestAPI 반환 모델
public class KeywordMapVO {
    private String id;                  // 장소 I
    @SerializedName("place_name")
    private String place_name;          // 장소명, 업체명
    private String category_name;       // 카테고리 이름
    private String category_group_code; // 중요 카테고리만 그룹핑한 카테고리 그룹 코드
    private String category_group_name; // 중요 카테고리만 그룹핑한 카테고리 그룹명
    private String phone;               // 전화번호
    private String address_name;        // 전체 지번 주소
    @SerializedName("road_address_name")
    private String road_address_name;   // 전체 도로명 주소
    private String x;                   // X 좌표값 (경도)
    private String y;                   // Y 좌표값 (위도)
    private String place_url;           // 장소 상세페이지 URL
    private String distance;            // 중심좌표까지의 거리 (요청 파라미터로 x, y를 준 경우만 존재)
}
