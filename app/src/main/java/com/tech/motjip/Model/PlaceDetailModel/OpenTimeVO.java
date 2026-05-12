package com.tech.motjip.Model.PlaceDetailModel;

import lombok.Data;

@Data
// 요일별 영업시간 정보
public class OpenTimeVO {
    private String day;   // "월(5/11)"
    private String time;  // "11:00 ~ 22:00"
}
