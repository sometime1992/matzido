package com.tech.motjip.Utils;

import android.util.Base64;
import android.util.Log;
import java.nio.charset.StandardCharsets;

public class JwtUtils {
    private static final String TAG = "JwtUtilsDebug";

    public static void printJwtPayload(String jwtToken) {
        try {
            if (jwtToken == null || !jwtToken.contains(".")) {
                return;
            }
            String[] parts = jwtToken.split("\\.");
            if (parts.length > 1) {
                String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE), StandardCharsets.UTF_8);
                Log.d(TAG, "--- JWT Payload (내부 정보) ---");
                Log.d(TAG, payload);
                Log.d(TAG, "-----------------------------");
            }
        } catch (Exception e) {
            Log.e(TAG, "JWT Payload 파싱 실패", e);
        }
    }
}