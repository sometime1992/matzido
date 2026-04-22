package com.tech.motjip.Log;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.jsoup.Connection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 테스트용 로그 관련 클래스
 */
public class AppLog {

    /**
     * 공용 데이터 출력용
     */
    public void printSharedPreferences(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        Map<String, ?> map= sharedPreferences.getAll();

        if (isMapEmpty(map)) {
            Log.d("SharedPreferences 로그", "값 비어 있음");
            return;
        }

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            Log.d("SharedPreferences 로그", "키 값: " + entry.getKey());
            Log.d("SharedPreferences 로그", "밸류 값: "+ String.valueOf(entry.getValue()));
        }
    }

    /**
     * 클래스 변수값 출력용
     */
    public <T> Map<String, String> getClassOfString(T obj){

        Map<String, String> result = new HashMap<>();

        try{
            Class<T> instance = (Class<T>)obj.getClass();

            Method[] getter = instance.getDeclaredMethods();

            String methodName = "get";

            for(Method method : getter){
                if(method.getName().startsWith(methodName) && method.getReturnType() == String.class){
                    method.setAccessible(true);
                    result.put(method.getName().substring(3), (String)method.invoke(obj));
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }
    /**
     * 맵 배열 출력용
     */
    public <K, V> void printMap(Map<K, V> map){
        if (isMapEmpty(map)) {
            Log.d("맵 로그", "맵 배열 비었음");
            return;
        }

        for (Map.Entry<K, V> entry : map.entrySet()) {
            Log.d("맵 로그", "키 값: " + entry.getKey());
            Log.d("맵 로그", "밸류 값: "+ entry.getValue());
        }
    }

    /**
     * response 값 테스트용
     */
    public void printExecute(Connection.Response response){
        Log.d("response 주소", String.valueOf(response.url()));
        Log.d("response 응답코드",String.valueOf(response.statusCode()));
        response.headers().keySet().forEach(s -> Log.d("response 헤더",s + " / "+ response.headers().get(s)));
        response.cookies().keySet().forEach(s-> Log.d("response 쿠키",s+ " / "+ response.cookies().get(s)));
        Log.d("response 보디",response.body());
    }

    /**
     * 맵배열 널체크
     */
    private boolean isMapEmpty(Map<?, ?> map){
        return map == null || map.isEmpty();
    }
}
