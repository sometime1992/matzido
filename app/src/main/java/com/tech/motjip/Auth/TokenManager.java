package com.tech.motjip.Auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TokenManager {

    private static final String TAG = "TokenManager";

    private static final String PREF_NAME = "AppPrefs";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveTokens(String accessToken, String refreshToken) {

        if (accessToken == null || refreshToken == null) {
            Log.e(TAG, "토큰 저장 실패: accessToken 또는 refreshToken이 null");
            return;
        }

        prefs.edit()
                .putString(ACCESS_TOKEN, accessToken)
                .putString(REFRESH_TOKEN, refreshToken)
                .apply();

        Log.d(TAG, "토큰 저장 완료");
    }

    public String getAccessToken() {
        return prefs.getString(ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(REFRESH_TOKEN, null);
    }

    public boolean hasAccessToken() {

        String token = getAccessToken();

        return token != null && !token.isEmpty();
    }

    public void clearTokens() {
        prefs.edit().clear().apply();
        Log.d(TAG, "토큰 삭제 완료");
    }
}