package com.tech.motjip.Auth;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final TokenManager tokenManager;

    public AuthInterceptor(Context context) {
        this.tokenManager = new TokenManager(context);
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        // 1. 저장된 엑세스 토큰 꺼내기
        String token = tokenManager.getAccessToken();

        Request.Builder builder = chain.request().newBuilder();

        // 2. 토큰이 있다면 헤더에 "Bearer {token}" 추가
        if (token != null && !token.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        return chain.proceed(builder.build());
    }
}