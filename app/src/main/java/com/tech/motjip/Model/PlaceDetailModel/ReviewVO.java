package com.tech.motjip.Model.PlaceDetailModel;

import lombok.Data;

@Data
// 카카오맵 리뷰 정보 (작성자 정보 포함)
public class ReviewVO {
    private int starRating;          // 별점 (1~5)
    private String contents;          // 리뷰 본문
    private String updatedAt;         // 수정일자

    // 작성자 정보
    private String nickname;          // 작성자 닉네임
    private String profileImageUrl;   // 작성자 프로필 이미지
    private int reviewCount;          // 작성자가 쓴 총 리뷰 수
    private double averageScore;      // 작성자가 매긴 평균 별점
}
