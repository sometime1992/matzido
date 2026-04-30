package com.tech.motjip.Model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// 맵의 위도 경도의 대한 좌표값 모델
public class MapPostionVO {
    private double latY;
    private double lngX;
}
