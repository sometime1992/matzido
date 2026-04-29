package com.tech.motjip.API.KakaoMap.KakaoHeader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tech.motjip.API.HttpHelper.DoConnect;

import org.jsoup.Connection;

import java.util.HashMap;
import java.util.Map;

public class DoTest {

//    public static JsonObject TestGetData(){
//        String uri = "https://dapi.kakao.com/v2/local/search/keyword.json";
//        Map<String, String> query = new HashMap<String, String>();
//        query.put("query", "카카오 프렌즈");
//        try{
//            Connection.Response result = DoConnect.get(uri, query);
//            if (result.statusCode() != 200) {
//                System.out.println("에러 발생 코드: " + result.statusCode());
//                System.out.println("에러 상세 내용: " + result.body()); // 카카오가 보낸 에러 JSON 출력
//            }
//
//            return new Gson().fromJson(result.body(), JsonObject.class);
//        }catch(Exception e){
//            e.printStackTrace();
//            return null;
//        }
//    }
}
