package com.tech.motjip.Utils;

import java.util.regex.Pattern;

// URL 형식 검증 유틸
public class UrlChecker {

    // 카카오 장소 URL 형식: http(s)://place.map.kakao.com/{숫자}
    private static final Pattern PLACE_URL_PATTERN = Pattern.compile("https?://place\\.map\\.kakao\\.com/\\d+.*");

    // 카카오 장소 URL 형식이 맞는지 검증
    public static boolean isPlaceUrl(String url) {
        return url != null && PLACE_URL_PATTERN.matcher(url).matches();
    }
}
