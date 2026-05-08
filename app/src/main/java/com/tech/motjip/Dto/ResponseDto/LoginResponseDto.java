package com.tech.motjip.Dto.ResponseDto;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponseDto implements Serializable {

    private Long memberId;
    private String email;
    private String nickname;

    private String accessToken;
    private String refreshToken;   // 🔥 추가

    private String profileImgUrl;  // 🔥 추가 (서버 already 사용 중)

    private boolean isNewUser;

    @Builder
    public LoginResponseDto(Long memberId,
                            String email,
                            String nickname,
                            String accessToken,
                            String refreshToken,
                            String profileImgUrl,
                            boolean isNewUser) {

        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.profileImgUrl = profileImgUrl;
        this.isNewUser = isNewUser;
    }

    // 🔥 기존 회원용 생성 메서드
    public static LoginResponseDto createForExistingMember(Long memberId,
                                                           String accessToken,
                                                           String refreshToken,
                                                           String nickname) {

        return LoginResponseDto.builder()
                .memberId(memberId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .nickname(nickname)
                .isNewUser(false)
                .build();
    }
}