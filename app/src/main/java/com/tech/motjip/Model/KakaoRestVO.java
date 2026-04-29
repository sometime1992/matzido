package com.tech.motjip.Model;

import lombok.Getter;

@Getter
public class KakaoRestVO {
    // 카카오 RestAPI의 기본 도메인 입니다.
    private final static String domain = "https://dapi.kakao.com/v2/";

    // 키워드로 검색하고 데이터를 반환합니다
    // https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword
    private final String mapSearch = domain + "local/search/keyword.json";

}
