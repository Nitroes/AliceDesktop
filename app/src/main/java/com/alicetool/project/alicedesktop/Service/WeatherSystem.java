package com.alicetool.project.alicedesktop.Service;

import android.content.Context;

import com.alicetool.project.httpclient.HttpClient;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherSystem {
    //京东万象
//    private String key="729ec8723eab765d4603db15dfc12356";
    private String ak="QLUqUPCWNXG9ODviM9N5ZjaShR5XpnSw";
    private String mcode="1E:89:FF:9D:25:F3:EF:3C:23:53:46:79:2A:6D:C5:A7:99:65:BD:C8;com.alicetool.project.alicedesktop";
    private HttpClient httpClient=new HttpClient();
    public JSONObject result;
    private SQLHelper sqlHelper;
    private Context context;


    public WeatherSystem(Context context){
        this.context=context;
        sqlHelper=SQLHelper.getInit(context);
    }

    public interface GetData{
        void backData(JSONObject data) throws JSONException;
    }

    public void GetWeather(String city,GetData getData){
//        httpClient.Get("https://way.jd.com/jisuapi/weather?city="+city+"&appkey="+key, text -> {
//            result=new JSONObject(text);
//            result=result.getJSONObject("result").getJSONObject("result");
//            getData.backData(result);
//        });

        String adcode=sqlHelper.getLocation();
        if (adcode.equals("")){
            Location location=new Location(context);
            location.getLocation(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    String code = bdLocation.getAdCode();
                    sqlHelper.putLocation(code);
                    sqlHelper.finish();
                    GetHttp(getData, code);
                }
            });
        }else {
            GetHttp(getData, adcode);
        }



    }

    private void GetHttp(GetData getData, String adcode) {
        httpClient.Get("http://api.map.baidu.com/weather/v1/?district_id="+adcode+"&data_type=all&ak="+ak+"&mcode="+mcode, text -> {
            result=new JSONObject(text);
            result=result.getJSONObject("result");
            getData.backData(result);
            sqlHelper.putLastWeather(String.valueOf(result));
        });
    }

}
