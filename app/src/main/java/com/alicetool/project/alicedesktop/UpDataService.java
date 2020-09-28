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
    public void onCreate() {
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
                        views.setTextViewText(R.id.textView,data.getString("temp") +"℃");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    widgetManager.updateAppWidget(name, views);//更新Widget
                });

            }
        };
        timer.scheduleAtFixedRate(task,1000,20000);

        super.onCreate();
    }
}
