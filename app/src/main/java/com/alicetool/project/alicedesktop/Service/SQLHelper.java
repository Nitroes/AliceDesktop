package com.alicetool.project.alicedesktop.Service;

import android.content.Context;
import android.content.SharedPreferences;

public class SQLHelper {
    public static SharedPreferences sharedPreferences=null;
    public static SQLHelper getInit (Context context){
        if (sharedPreferences==null)
            sharedPreferences=context.getSharedPreferences("Setting",Context.MODE_PRIVATE);
        return new SQLHelper();
    }

    public String getString(String key){
        return sharedPreferences.getString(key,null);
    }

    public void putString(String key,String value){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

}
