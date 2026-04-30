package com.tech.motjip.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

public class JsonHelper {
    // 원본 HTML문자열에서 JSON object를 반환합니다.
    public static JsonObject getJsonFromString(String htmlBody){
        return new Gson().fromJson(htmlBody, JsonObject.class);
    }

    // @ 오버로드, 원본 HTML문자열에서 JSON object의 배열 데이터를 반환합니다.
    public static <T> List<T> getJsonFromString(String htmlBody, String arrayName, Class<T[]> arrayClass) {
        // html에서 Json데이터를 가져옵니다.
        JsonObject json = getJsonFromString(htmlBody);

        // 키워드에 해당하는 Json 배열을 가져옵니다.
        String jsonArrayString = json.getAsJsonArray(arrayName).toString();

        // Json을 java 배열로 파싱합니다.
        T[] parsedArray = new Gson().fromJson(jsonArrayString, arrayClass);

        // 파싱된 배열을 리스트 객체로 반환합니다.
        return Arrays.asList(parsedArray);
    }
    // JSON객체가 null인지를 검증합니다.
    public static boolean isJsonEmpty(JsonObject jsonObject){
        return (jsonObject == null || jsonObject.isJsonNull() || jsonObject.isEmpty());
    }
}
