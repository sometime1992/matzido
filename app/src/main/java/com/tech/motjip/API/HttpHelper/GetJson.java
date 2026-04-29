package com.tech.motjip.API.HttpHelper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tech.motjip.API.CustomException.KakaoRestException;
import com.tech.motjip.Model.KakaoRestVO;
import com.tech.motjip.Utils.JsonHelper;

import org.jsoup.Connection;

import java.util.HashMap;
import java.util.Map;

public class GetJson {

    private final static KakaoRestVO vo = new KakaoRestVO();

    public static String GetMapSearchData(String query){
        Map<String, String> hashQuery = new HashMap<String, String>();
        hashQuery.put("query", query);
        try{
            Connection.Response result = DoConnect.get(vo.getMapSearch(), KakaoRestHeader.getHeader(), hashQuery);
            if (result.statusCode() != 200) {
                throw new KakaoRestException(result);
            }
            return JsonHelper.getJsonFromString(result.body()).toString();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
