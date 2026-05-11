package com.tech.motjip.Handler;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "motjip_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_EMAIL = "user_email"; // 🚀 이메일 저장을 위한 키

    // 💰 토큰과 이메일을 한꺼번에 저장하기
    public static void saveTokens(Context context, String accessToken, String refreshToken, String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_USER_EMAIL, email); // 🚀 이메일도 꼭 저장해야 합니다!
        editor.apply();
    }

    // 🔑 저장된 액세스 토큰 가져오기
    public static String getAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    // 📧 저장된 이메일 가져오기 (지금 빨간 줄 뜨는 부분 해결!)
    public static String getUserEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    // 🚪 로그아웃 시 모든 정보 지우기
    public static void clearTokens(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    // PreferenceManager.java 에 추가
    private static final String KEY_USER_NICKNAME = "user_nickname"; // 🚀 키 추가

    // 닉네임 저장
    public static void saveNickname(Context context, String nickname) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_NICKNAME, nickname).apply();
    }

    // 닉네임 가져오기
    public static String getNickname(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_NICKNAME, null);
    }


}