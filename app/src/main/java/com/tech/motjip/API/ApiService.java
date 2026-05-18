package com.tech.motjip.API;

import com.tech.motjip.Dto.RequestDto.KakaoSdkLoginRequestDto;
import com.tech.motjip.Dto.RequestDto.LogoutRequestDto;
import com.tech.motjip.Dto.RequestDto.NicknameUpdateRequestDto;
import com.tech.motjip.Dto.RequestDto.RefreshRequestDto;
import com.tech.motjip.Dto.RequestDto.StatusUpdateRequestDto;
import com.tech.motjip.Dto.RequestDto.UpdateMyNicknameRequestDto;

import com.tech.motjip.Dto.ResponseDto.CommunityPostPageResponse;
import com.tech.motjip.Dto.ResponseDto.LoginResponseDto;
import com.tech.motjip.Dto.ResponseDto.TokenResponseDto;

import com.tech.motjip.Model.ChatRoom;
import com.tech.motjip.Model.Message;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @GET("/api/chat/rooms")
    Call<List<ChatRoom>> getChatRoomList();

    @GET("/api/chat/messages/{roomId}")
    Call<List<Message>> getChatMessages(
            @Path("roomId") Long roomId
    );

    @POST("/api/chat/rooms")
    Call<ChatRoom> createChatRoom(
            @Body ChatRoom chatRoom
    );

    // 게시글 등록
    @Multipart
    @POST("/api/v1/community/posts")
    Call<Void> createCommunityPost(
            @Part("tag") RequestBody tag,
            @Part("region") RequestBody region,
            @Part("title") RequestBody title,
            @Part("location") RequestBody location,
            @Part("date") RequestBody date,
            @Part("content") RequestBody content,
            @Part MultipartBody.Part image
    );

    // 게시글 조회 + 페이징
    @GET("/api/v1/community/posts")
    Call<CommunityPostPageResponse> getCommunityPosts(
            @Query("title") String title,
            @Query("tag") String tag,
            @Query("region") String region,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("size") int size
    );

    // 즐겨찾기 추가 / 취소 toggle
    @POST("/api/v1/favorites/community/{comId}/toggle")
    Call<Boolean> toggleFavoriteCommunityPost(
            @Path("comId") Long comId
    );

    // 모임 참여
    @POST("/api/v1/community/posts/{comId}/join")
    Call<Void> joinCommunity(
            @Path("comId") Long comId
    );
}