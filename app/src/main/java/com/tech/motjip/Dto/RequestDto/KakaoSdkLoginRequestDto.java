package com.tech.motjip.Dto.RequestDto;

public class KakaoSdkLoginRequestDto {

    private String accessToken;

    public KakaoSdkLoginRequestDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}