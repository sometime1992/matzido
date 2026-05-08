package com.tech.motjip.Dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NicknameUpdateRequestDto {
    private Long memberId;
    private String nickname;
}