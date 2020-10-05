package com.alicetool.project.alicedesktop;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.alicetool.project.alicedesktop.Service.WeatherSystem;

import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

public class UpDataService extends Service {

    private Timer timer;
    private TimerTask task;
    private AppWidgetManager widgetManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = new Timer();
        widgetManager = AppWidgetManager.getInstance(getApplicationContext());
        task=new TimerTask() {
            @Override
            public void run() {
                WeatherSystem weather=new WeatherSystem();
                weather.GetWeather("深圳", data -> {
                    ComponentName name = new ComponentName(UpDataService.this.getPackageName(),
                            DesktopWidget.class.getName());// 获取前面参数包下的后参数的Widget
                    RemoteViews views = new RemoteViews(UpDataService.this.getPackageName(),
                            R.layout.desktop_widget);// 获取Widget的布局
                    try {
                        views.setTextViewText(R.id.temp,data.getString("temp") +"℃");
                        views.setTextViewText(R.id.humidity,data.getString("humidity") +"Rh");
                        views.setTextViewText(R.id.city,data.getString("city"));
                        views.setTextViewText(R.id.weather,data.getString("weather"));
                        views.setTextViewText(R.id.wind,data.getString("winddirect"));
                        views.setTextViewText(R.id.windPower,data.getString("windpower"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    widgetManager.updateAppWidget(name, views);//更新Widget
                });

            }
        };
        timer.scheduleAtFixedRate(task,1000,60000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {


        super.onCreate();
    }
}
