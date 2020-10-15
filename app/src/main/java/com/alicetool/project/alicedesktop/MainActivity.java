package com.alicetool.project.alicedesktop;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alicetool.project.alicedesktop.Service.SQLHelper;
import com.alicetool.project.alicedesktop.Service.WeatherSystem;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LineChart lineChart;
    JSONObject weather=null;
    SQLHelper sqlHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqlHelper=SQLHelper.getInit(getApplicationContext());
        try {
            weather=new JSONObject(sqlHelper.getLastWeather());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (weather==null){
            WeatherSystem weatherSystem=new WeatherSystem(getApplicationContext());
            weatherSystem.GetWeather(sqlHelper.getLocation(), data -> {
                weather=data;
                ChartInit();
            });
        }else {ChartInit();}
        
    }

    private void ChartInit() {
        List<Entry> high=new ArrayList<>();
        List<Entry> low=new ArrayList<>();
        try {
            JSONArray jsonArray=weather.getJSONArray("forecasts");
            for (int i = 0; i <jsonArray.length() ; i++) {
                JSONObject json=jsonArray.getJSONObject(i);
                long date=DateToLong(json.getString("date"));
                System.out.println(LongToDate(date)+" = " + date);
                high.add(new Entry(date,json.getInt("high")));
                low.add(new Entry(date,json.getInt("low")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        LineData lineData=new LineData(
                getLineDataSet(high, "最高温度",Color.BLUE),
                getLineDataSet(low, "最低温度",Color.RED)
        );

        lineChart=findViewById(R.id.line_chart);
        XAxis xAxis=lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return LongToDate((long) value);
            }
        });
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setTouchEnabled(false);
        Description description=new Description();
        description.setText("未来四天温度");
        lineChart.setDescription(description);
        lineChart.setData(lineData);
    }

    private long DateToLong(String dateStr) {
        SimpleDateFormat pattern = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = pattern.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    private String LongToDate(long dateLong) {
        SimpleDateFormat pattern = new SimpleDateFormat("MM-dd");
        return pattern.format(new Date(dateLong));
    }

    private LineDataSet getLineDataSet(List<Entry> data, String high,int color) {
        LineDataSet dataSet = new LineDataSet(data, high);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawCircles(false);
        dataSet.setColor(color);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int)value+"℃";
            }
        });
        return dataSet;
    }


}
