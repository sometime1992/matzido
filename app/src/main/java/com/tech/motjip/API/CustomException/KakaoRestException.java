package com.tech.motjip.API.CustomException;

import org.jsoup.Connection;

// 카카오 RestAPI에서 발생되는 에러와 예외를 처리합니다.
public class KakaoRestException extends Exception{

    public KakaoRestException(Connection.Response result)
    {
        super("에러 내용: " + result.body());
    }

    public KakaoRestException(String message, Throwable cause) {
        super(message, cause);
    }

    public KakaoRestException(Throwable cause)
    {
        super(cause);
    }
}
