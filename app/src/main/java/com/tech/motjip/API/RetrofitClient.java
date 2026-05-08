package com.tech.motjip.API;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tech.motjip.Dto.RequestDto.RefreshRequestDto;
import com.tech.motjip.Dto.ResponseDto.TokenResponseDto;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String TAG = "RetrofitClientDebug";
    private static final String BASE_URL = "https://spout-distant-cost.ngrok-free.dev/";

    private static final String PREF_NAME = "AppPrefs";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    public static ApiService getApiService(Context context) {

        if (apiService == null || retrofit == null) {

            Context appContext = context.getApplicationContext();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor authInterceptor = chain -> {

                SharedPreferences prefs =
                        appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                String accessToken = prefs.getString(ACCESS_TOKEN, null);
                String refreshToken = prefs.getString(REFRESH_TOKEN, null);

                Request originalRequest = chain.request();

                // refresh 요청 자체에는 다시 refresh 시도하지 않음
                boolean isRefreshRequest =
                        originalRequest.url().encodedPath().contains("/api/v1/auth/refresh");

                Request.Builder requestBuilder = originalRequest.newBuilder();

                if (!isRefreshRequest && accessToken != null && !accessToken.trim().isEmpty()) {
                    requestBuilder.header("Authorization", "Bearer " + accessToken);
                }

                Response response = chain.proceed(requestBuilder.build());

                if (response.code() == 401 && !isRefreshRequest) {
                    Log.w(TAG, "AccessToken 만료 또는 인증 실패 → refresh 시도");

                    response.close();

                    if (refreshToken == null || refreshToken.trim().isEmpty()) {
                        Log.e(TAG, "RefreshToken 없음 → 로그아웃 필요");
                        clearTokens(prefs);
                        return response;
                    }

                    String newAccessToken = refreshAccessToken(appContext, refreshToken);

                    if (newAccessToken == null) {
                        Log.e(TAG, "토큰 재발급 실패 → 로그아웃 필요");
                        clearTokens(prefs);
                        return response;
                    }

                    Log.d(TAG, "토큰 재발급 성공 → 원래 요청 재시도");

                    Request retryRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + newAccessToken)
                            .build();

                    return chain.proceed(retryRequest);
                }

                return response;
            };

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }

        return apiService;
    }

    private static String refreshAccessToken(Context context, String refreshToken) {

        try {
            OkHttpClient refreshClient = new OkHttpClient.Builder().build();

            Retrofit refreshRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(refreshClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService refreshApiService = refreshRetrofit.create(ApiService.class);

            retrofit2.Response<TokenResponseDto> refreshResponse =
                    refreshApiService.refreshToken(new RefreshRequestDto(refreshToken)).execute();

            if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {

                TokenResponseDto tokenResponse = refreshResponse.body();

                String newAccessToken = tokenResponse.getAccessToken();
                String newRefreshToken = tokenResponse.getRefreshToken();

                if (newAccessToken == null || newRefreshToken == null) {
                    return null;
                }

                SharedPreferences prefs =
                        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                prefs.edit()
                        .putString(ACCESS_TOKEN, newAccessToken)
                        .putString(REFRESH_TOKEN, newRefreshToken)
                        .apply();

                return newAccessToken;
            }

        } catch (IOException e) {
            Log.e(TAG, "refreshAccessToken IOException", e);
        } catch (Exception e) {
            Log.e(TAG, "refreshAccessToken Exception", e);
        }

        return null;
    }

    private static void clearTokens(SharedPreferences prefs) {
        prefs.edit()
                .remove(ACCESS_TOKEN)
                .remove(REFRESH_TOKEN)
                .apply();
    }
}