package com.tech.motjip.API.HttpHelper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.Map;

public class DoConnect {
    public static Connection.Response get(String url, Map<String, String> header, Map<String, String> query) throws Exception{
        Connection.Response response = Jsoup.connect(url)
                .headers(header)
                .method((Connection.Method.GET))
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .data(query)
                .execute();

        return response;
    }
}
