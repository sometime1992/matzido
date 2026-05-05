package com.tech.motjip.Model;

public class MemberJoinRequest {
    public String email;
    public String nickname;
    public String profileImageUrl;
    public int statusCode;
    public Long providerId;

    // 생성자 (Constructor)
    public MemberJoinRequest(String email, String nickname, String profileImageUrl, int statusCode, Long providerId) {
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.statusCode = statusCode;
        this.providerId = providerId;
    }
}