package com.alicetool.project.alicedesktop;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.RemoteViews;

import com.alicetool.project.alicedesktop.Service.WeatherSystem;

import org.json.JSONException;

import java.util.ArrayList;


/**
 * Implementation of App Widget functionality.
 */
public class DesktopWidget extends AppWidgetProvider {

    private int requestCode=200;
    private BroadcastReceiver screenBroadcast=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                new Thread(()->{
                    try {
                        Thread.sleep(5000);
                        UpWeatherData(context);
                        Log.i("WidgetStatus", "onUpdate");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                Log.i("WidgetStatus", "ACTION_SCREEN_ON");
                UpWeatherData(context);
            }

        }
    };


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.desktop_widget);
        appWidgetManager.updateAppWidget(appWidgetId, views);
        Log.i("WidgetStatus", "updateAppWidget");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        new Thread(()->{
            try {
                Thread.sleep(10000);
                UpWeatherData(context);
                Log.i("WidgetStatus", "onUpdate");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
//        Intent intent = new Intent(context, UpDataService.class);
//        context.startService(intent);
        UpWeatherData(context);
        Log.i("WidgetStatus", "onEnabled");

//        IntentFilter updateIntent = new IntentFilter();
//        updateIntent.addAction(Intent.ACTION_TIME_TICK);
//        context.getApplicationContext().registerReceiver(timeBroadcast, updateIntent);

        alarmManagerInit(context,(alarmManager,pendIntent)->{
            alarmManager.setRepeating(AlarmManager.RTC,System.currentTimeMillis(),120000,pendIntent);
        });
        registSreenStatusReceiver(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)&&
                requestCode==intent.getIntExtra("requestCode",0)){
            Log.i("WidgetStatus", "onReceive");
            UpWeatherData(context);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
//        Intent intent = new Intent(context, UpDataService.class);
//        context.stopService(intent);
//        System.out.println("end");
        alarmManagerInit(context,(alarmManager,pendIntent)->{
            alarmManager.cancel(pendIntent);
        });

        Log.i("WidgetStatus", "onDisabled");
    }

    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }


    private void UpWeatherData(Context context){
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        WeatherSystem weather=new WeatherSystem();
        weather.GetWeather("深圳", data -> {
            ComponentName name = new ComponentName(context.getApplicationContext(),
                    DesktopWidget.class);// 获取前面参数包下的后参数的Widget
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.desktop_widget);// 获取Widget的布局
            try {
                views.setTextViewText(R.id.temp,data.getString("temp") +"℃");
                views.setTextViewText(R.id.humidity,data.getString("humidity") +"Rh");
                views.setTextViewText(R.id.city,data.getString("city"));
                views.setTextViewText(R.id.weather,data.getString("weather"));
                views.setTextViewText(R.id.wind,data.getString("winddirect"));
                views.setTextViewText(R.id.windPower,data.getString("windpower"));

                //点击跳转
                Intent skinIntent=new Intent(context,MainActivity.class);
                PendingIntent pIntent=PendingIntent.getActivity(context,200,skinIntent,PendingIntent.FLAG_CANCEL_CURRENT);
                views.setOnClickPendingIntent(R.id.widgetView,pIntent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            widgetManager.updateAppWidget(name, views);//更新Widget
        });
    }

    private void registSreenStatusReceiver(Context context) {
        IntentFilter updateIntent = new IntentFilter();
        updateIntent.addAction(Intent.ACTION_SCREEN_ON);
        context.getApplicationContext().registerReceiver(screenBroadcast, updateIntent);
    }

    interface alarmManagerOnInit{
        void onInit(AlarmManager alarmManager,PendingIntent pendingIntent);
    }

    private void alarmManagerInit(Context context,alarmManagerOnInit alarmManagerOnInit){
        AlarmManager alarmManager=  (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra("requestCode",requestCode);
        PendingIntent pendIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManagerOnInit.onInit(alarmManager,pendIntent);
    }

}

