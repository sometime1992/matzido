package com.tech.motjip.Dto.RequestDto;

public class UpdateMyNicknameRequestDto {

    private String nickname;

    public UpdateMyNicknameRequestDto(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}