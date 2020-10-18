package com.alicetool.project.alicedesktop.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alicetool.project.sql.SQL;

import org.json.JSONArray;
import org.json.JSONObject;

public class SQLHelper extends SQLiteOpenHelper {


    public static SharedPreferences sharedPreferences=null;
    private String TABLE_WEATHER="WEATHER_DATA";

    public SQLHelper(Context context) {
        super(context, "Env_Weather", null, 1);
    }

    public static SQLHelper getInit (Context context){
        if (sharedPreferences==null)
            sharedPreferences=context.getSharedPreferences("Setting",Context.MODE_PRIVATE);
        return new SQLHelper(context);
    }

    public String getString(String key){
        return sharedPreferences.getString(key,"");
    }

    public void putString(String key,String value){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public String getLocation(){return getString("adcode");}

    public void putLocation(String value){ putString("adcode",value);}

    public String getLastWeather(){return getString("lastWeather");}

    public void putLastWeather(String value){ putString("lastWeather",value);}


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            SQL.createTable(sqLiteDatabase,TABLE_WEATHER,new JSONObject()
                    .put("area","varchar(20)")//地区
                    .put("temp","tinyint")//温度
                    .put("feels_like","tinyint")//体感温度
                    .put("rh","tinyint")//相对湿度
                    .put("wind_class","varchar(20)")//风力等级
                    .put("wind_dir","varchar(20)")//风向描述
                    .put("text","varchar(20)")//天气现象
                    .put("date","DATETIME default (datetime('now', 'localtime'))")//时间
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void insertWeather(String location,JSONObject now) throws Exception {
        SQL.insert(getWritableDatabase(),TABLE_WEATHER,new JSONObject()
                .put("area",location)//地区
                .put("temp",now.getString("temp"))
                .put("feels_like",now.getString("feels_like"))//体感温度
                .put("rh",now.getString("rh"))//相对湿度
                .put("wind_class",now.getString("wind_class"))//风力等级
                .put("wind_dir",now.getString("wind_dir"))//风向描述
                .put("text",now.getString("text"))//天气现象
        );
    }

    public void finish(){
        close();
    }

    public JSONArray getBeforeWeather(String[] select) throws Exception {
        return SQL.select(getReadableDatabase(),TABLE_WEATHER,select);
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
