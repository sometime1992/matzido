package com.tech.motjip.Dto.RequestDto;

public class LogoutRequestDto {

    private String refreshToken;

    public LogoutRequestDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}