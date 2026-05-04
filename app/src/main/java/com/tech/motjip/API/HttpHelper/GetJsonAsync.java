package com.tech.motjip.API.HttpHelper;

import com.tech.motjip.Model.KeywordMapVO;
import com.tech.motjip.Model.MapPostionVO;
import com.tech.motjip.Thread.IThreadReturn1Callback;

import java.util.List;

// Http통신을 통해 특정 Json을 비동기로 가져옵니다
public class GetJsonAsync {

    public static void GetMapSearchDataAsync(String query, IThreadReturn1Callback<List<KeywordMapVO>> callback){
        new Thread(() -> {
            try {
                List<KeywordMapVO> data = GetJson.GetMapSearchData(query);
                callback.ThreadEnds(data);
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    public static void GetMapSearchDataWithConditionsAsync(String query, String x, String y, String radius, IThreadReturn1Callback<List<KeywordMapVO>> callback){
        new Thread(() -> {
            try {
                List<KeywordMapVO> data = GetJson.GetMapSearchDataWithConditions(query, x, y, radius);
                callback.ThreadEnds(data);
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    public static void GetMapSearchDataWithConditions(String query, MapPostionVO position, String radius, IThreadReturn1Callback<List<KeywordMapVO>> callback){
        GetMapSearchDataWithConditionsAsync(query, Double.toString(position.getLngX()), Double.toString(position.getLatY()), radius, callback);
    }
}
