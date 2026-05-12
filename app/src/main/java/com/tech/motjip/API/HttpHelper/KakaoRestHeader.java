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

    // 웹 브라우저 흉내내는 헤더 처리 (크롤링용)
    public static Map<String, String> getWebHeader(){
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Mobile Safari/537.36");
        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
        header.put("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8");
        header.put("Accept-Encoding", "gzip, deflate, br");

        return header;
    }

    // 카카오 장소 상세 API용 헤더 (Referer + pf만 있으면 통과)
    protected static Map<String, String> getPlaceApiHeader(){
        Map<String, String> header = new HashMap<>();
        header.put("Referer", "https://place.map.kakao.com/");
        header.put("pf", "PC");

        return header;
    }
}
