package com.alicetool.project.alicedesktop.Service;

import com.alicetool.project.httpclient.HttpClient;

import org.json.JSONObject;

public class WeatherSystem {
    //京东万象
    private String key="729ec8723eab765d4603db15dfc12356";
    private HttpClient httpClient=new HttpClient();
    public JSONObject result;


    public interface GetData{
        void backData(JSONObject data);
    }

    public void GetWeather(String city,GetData getData){
        httpClient.Get("https://way.jd.com/jisuapi/weather?city="+city+"&appkey="+key, text -> {
            result=new JSONObject(text);
            result=result.getJSONObject("result").getJSONObject("result");
            getData.backData(result);
        });
    }

}
