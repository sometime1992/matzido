package com.tech.motjip.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginStateManager {

    private static final String PREF_NAME = "AppPrefs";
    private static final String LOGIN_STATUS = "LOGIN_STATUS";

    public static final int LOGOUT = 0;
    public static final int LOGIN = 1;

    // 로그인 상태 저장
    public static void setLoginStatus(
            Context context,
            int status
    ) {

        SharedPreferences prefs =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        prefs.edit()
                .putInt(
                        LOGIN_STATUS,
                        status
                )
                .apply();
    }

    // 로그인 상태 조회
    public static int getLoginStatus(
            Context context
    ) {

        SharedPreferences prefs =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        return prefs.getInt(
                LOGIN_STATUS,
                LOGOUT
        );
    }

    // 로그인 여부 반환
    public static boolean isLogin(
            Context context
    ) {

        return getLoginStatus(context) == LOGIN;
    }
}