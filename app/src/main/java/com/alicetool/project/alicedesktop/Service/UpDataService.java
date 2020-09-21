package com.alicetool.project.alicedesktop.Service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.alicetool.project.alicedesktop.R;


import java.util.Timer;
import java.util.TimerTask;

public class UpDataService extends Service {

    private Timer timer;
    private TimerTask task;
    private AppWidgetManager widgetmanager;

    public UpDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        timer = new Timer();
        widgetmanager = AppWidgetManager.getInstance(getApplicationContext());
        task=new TimerTask() {
            @Override
            public void run() {
                WeatherSystem weather=new WeatherSystem();
                weather.GetWeather("深圳", data -> {
                    System.out.println("-------------------");
                    ComponentName name = new ComponentName(UpDataService.this.getPackageName(),
                            UpDataService.class.getName());
                    RemoteViews views = new RemoteViews(UpDataService.this.getPackageName(),
                            R.layout.desktop_widget);
                    try {
                        views.setTextViewText(R.id.textView,"123");

                        System.out.println(data.getString("city"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    widgetmanager.updateAppWidget(name, views);
                });

            }
        };
        timer.scheduleAtFixedRate(task,1000,5000);

        super.onCreate();
    }
}
