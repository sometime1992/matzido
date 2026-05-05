package com.tech.motjip.Controller;



import com.tech.motjip.Model.MemberJoinRequest;
import com.tech.motjip.Model.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MotJipApiService {

    // 1. 회원가입 및 첫 로그인 통신 (기존 MainActivity에 있던 것)
    @POST("api/members/join")
    Call<TokenResponse> joinMember(@Body MemberJoinRequest request);

    //2. 프로필 설정 통신 (기존 ProfileActivity에 있던 것)

    // 🚀 로그아웃 API 추가
    @POST("api/members/logout")
    Call<Void> logout(@Body String email);

    @POST("api/members/update-profile")
    Call<TokenResponse> updateProfile(@Body MemberJoinRequest request);

    // 💡 내일 만들 '채팅 내역 불러오기' 같은 API 통신 로직도 앞으로 전부 여기에 추가됩니다!



}

