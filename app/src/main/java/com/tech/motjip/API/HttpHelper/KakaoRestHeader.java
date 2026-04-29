package com.tech.motjip.API.HttpHelper;

import java.util.HashMap;
import java.util.Map;

public class KakaoRestHeader {
    private final static String restKey = "b0b9a40e1c87fe6d5be83586b912a27a";

    // http 헤더 처리
    protected static Map<String, String> getHeader(){
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "KakaoAK " + restKey);

        return header;
    }
}
