package com.tech.motjip.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonHelper {
    // 원본 HTML문자열에서 JSON object를 반환합니다.
    public static JsonObject getJsonFromString(String htmlBody){
        return new Gson().fromJson(htmlBody, JsonObject.class);
    }
    // JSON객체가 null인지를 검증합니다.
    public static boolean isJsonEmpty(JsonObject jsonObject){
        return (jsonObject == null || jsonObject.isJsonNull() || jsonObject.isEmpty());
    }
}
