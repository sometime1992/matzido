package com.tech.motjip.API;

import com.tech.motjip.Dto.RequestDto.KakaoSdkLoginRequestDto;
import com.tech.motjip.Dto.RequestDto.LogoutRequestDto;
import com.tech.motjip.Dto.RequestDto.NicknameUpdateRequestDto;
import com.tech.motjip.Dto.RequestDto.RefreshRequestDto;
import com.tech.motjip.Dto.RequestDto.UpdateMyNicknameRequestDto;
import com.tech.motjip.Dto.ResponseDto.LoginResponseDto;
import com.tech.motjip.Dto.ResponseDto.TokenResponseDto;
import com.tech.motjip.Dto.RequestDto.StatusUpdateRequestDto;
//import com.tech.motjip.Model.ChatRoom;
//import com.tech.motjip.Model.Message;
//import java.util.List;
//import retrofit2.http.Path;

import okhttp3.MultipartBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @PATCH("/api/v1/auth/nickname")
    Call<LoginResponseDto> updateNickname(
            @Body NicknameUpdateRequestDto request
    );

    @PATCH("/api/v1/auth/me/nickname")
    Call<LoginResponseDto> updateMyNickname(
            @Body UpdateMyNicknameRequestDto request
    );

    @Multipart
    @PATCH("/api/v1/auth/me/profile-image")
    Call<LoginResponseDto> uploadProfileImage(
            @Part MultipartBody.Part image
    );

    @GET("/api/v1/user/me")
    Call<LoginResponseDto> getCurrentUser();

    @POST("/api/v1/auth/refresh")
    Call<TokenResponseDto> refreshToken(
            @Body RefreshRequestDto request
    );

    @POST("/api/v1/auth/logout")
    Call<Void> logout(
            @Body LogoutRequestDto request
    );

    @POST("/api/v1/auth/kakao")
    Call<LoginResponseDto> loginWithKakaoSdk(
            @Body KakaoSdkLoginRequestDto requestDto
    );

    @PATCH("/api/v1/auth/me/status")
    Call<Void> updateMyStatus(
            @Body StatusUpdateRequestDto request
    );

//    @GET("/api/chat/rooms")
//    Call<List<ChatRoom>> getChatRoomList();
//
//    @GET("/api/chat/messages/{roomId}")
//    Call<List<Message>> getChatMessages(@Path("roomId") Long roomId);
//
//    @POST("/api/chat/rooms")
//    Call<ChatRoom> createChatRoom(@Body ChatRoom chatRoom);

}