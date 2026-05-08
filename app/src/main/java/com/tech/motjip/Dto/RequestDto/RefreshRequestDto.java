package com.tech.motjip.Dto.RequestDto;

public class RefreshRequestDto {

    private String refreshToken;

    public RefreshRequestDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}