package com.tech.motjip.API.HttpHelper;

import java.util.HashMap;
import java.util.Map;

public class KakaoRestHeader {
    private final static String restKey = "9267fdc8361493f742543f1b9e05548f";

    // http 헤더 처리
    protected static Map<String, String> getHeader(){
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "KakaoAK " + restKey);

        return header;
    }
}
