package com.tech.motjip.Dto.ResponseDto;

public class TokenResponseDto {

    private String accessToken;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}