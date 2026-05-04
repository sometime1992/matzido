package com.tech.motjip;

import com.tech.motjip.API.HttpHelper.GetJson;
import com.tech.motjip.Model.KeywordMapVO;

import org.junit.Test;

import java.util.List;

public class KakaoAPITest {
    @Test
    public void getDataTest(){
        try{
            List<KeywordMapVO> vo = GetJson.GetMapSearchDataWithConditions("햄버거", "129.0192326360133","35.217951030549614","2000");
                System.out.println("통신 성공!");
                for(KeywordMapVO name : vo){
                    System.out.println(name.getPlace_name());
                    System.out.println(name.getX());
                    System.out.println(name.getY());
                }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
