package com.alicetool.project.alicedesktop;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alicetool.project.alicedesktop.Service.SQLHelper;
import com.alicetool.project.alicedesktop.Service.WeatherSystem;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.DataSet;
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

    LineChart lineChart,historyLineChart;
    JSONObject weather=null;
    SQLHelper sqlHelper;
    GridView predictionView;
    JSONArray jsonArray=null,historyJsonArray=null;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner=findViewById(R.id.spinner);
        predictionView=findViewById(R.id.prediction_view);
        lineChart=findViewById(R.id.line_chart);
        historyLineChart=findViewById(R.id.history_line_chart);

        sqlHelper=SQLHelper.getInit(getApplicationContext());
        try {
            weather=new JSONObject(sqlHelper.getLastWeather());

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (weather==null){
            WeatherSystem weatherSystem=new WeatherSystem(getApplicationContext());
            weatherSystem.GetWeather(sqlHelper.getLocation(), data -> {
                weather=data;
                ChartInit();
            });
        }else {
            ChartInit();
        }
        historyChart();



    }

    private void historyChart() {

        List<Entry> temp=new ArrayList<>();
        List<Entry> hum=new ArrayList<>();
        List<Long> time=new ArrayList<>();

        try {
            historyJsonArray=sqlHelper.getBeforeWeather(new String[]{"temp","rh","date"});
            for (int i = 0; i <historyJsonArray.length() ; i++) {
                JSONObject json=historyJsonArray.getJSONObject(i);
                temp.add(new Entry(i,json.getInt("temp")));
                hum.add(new Entry(i,json.getInt("rh")));

                time.add(DateToLong(json.getString("date"),"yyyy-MM-dd HH:mm:ss"));
            }
        }catch (Exception e){}

        XAxis xAxis=historyLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setLabelRotationAngle(60);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String text=LongToDate(time.get((int) value),"HH:mm");
                return text;
            }
        });
        historyLineChart.getAxisRight().setEnabled(false);

        Description description=new Description();
        description.setText("过去24内数据");
        historyLineChart.setDescription(description);
        LineDataSet tempDataSet=getLineDataSet(temp, "温度",Color.BLUE,"");
        LineDataSet humDataSet=getLineDataSet(hum, "湿度",Color.GREEN,"");
        LineData lineData=new LineData(tempDataSet);

        ArrayAdapter adapter=ArrayAdapter.createFromResource(this,R.array.array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lineData.removeDataSet(lineData.getDataSetCount()-1);
                if (i==0){
                    lineData.addDataSet(tempDataSet);
                }else {
                    lineData.addDataSet(humDataSet);
                }
                historyLineChart.notifyDataSetChanged();
                historyLineChart.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });







        historyLineChart.setData(lineData);


    }

    private void ChartInit() {
        List<Entry> high=new ArrayList<>();
        List<Entry> low=new ArrayList<>();
        List<Long> date=new ArrayList<>();
        try {
            jsonArray=weather.getJSONArray("forecasts");
            for (int i = 0; i <jsonArray.length() ; i++) {
                JSONObject json=jsonArray.getJSONObject(i);
                date.add(DateToLong(json.getString("date"),"yyyy-MM-dd"));
                high.add(new Entry(i,json.getInt("high")));
                low.add(new Entry(i,json.getInt("low")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        LineData lineData=new LineData(
                getLineDataSet(high, "最高温度",Color.RED,"℃"),
                getLineDataSet(low, "最低温度",Color.BLUE,"℃")
        );

        predictionView.setNumColumns(jsonArray.length());
        System.out.println("jsonArray.length() = " + jsonArray.length());
        predictionView.setAdapter(new BaseAdapter() {
            LayoutInflater inflater=LayoutInflater.from(getApplicationContext());

            @Override
            public int getCount() {
                return jsonArray.length();
            }

            @Override
            public Object getItem(int i) {
                try {
                    return jsonArray.get(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return i;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {

                if (view==null)
                    view =inflater.inflate(R.layout.prediction_item,null);

                TextView week=view.findViewById(R.id.week);
                TextView weather_day=view.findViewById(R.id.weather_day);
                TextView weather_night=view.findViewById(R.id.weather_night);
                TextView wind_power_day=view.findViewById(R.id.wind_power_day);
                TextView wind_power_night=view.findViewById(R.id.wind_power_night);
                TextView wind_day=view.findViewById(R.id.wind_day);
                TextView wind_night=view.findViewById(R.id.wind_night);
                try {
                    JSONObject data= jsonArray.getJSONObject(i);
                    week.setText(data.getString("week"));
                    weather_day.setText(data.getString("text_day"));
                    weather_night.setText(data.getString("text_night"));
                    wind_power_day.setText(data.getString("wc_day"));
                    wind_power_night.setText(data.getString("wc_night"));
                    wind_day.setText(data.getString("wd_day"));
                    wind_night.setText(data.getString("wd_night"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return view;
            }

        });

        XAxis xAxis=lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String time=LongToDate(date.get((int) value),"MM-dd");
                return time;
            }
        });
        xAxis.setLabelCount(5,true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setTouchEnabled(false);

        Description description=new Description();
        description.setText("未来四天温度");
        lineChart.setDescription(description);
        lineChart.setData(lineData);
    }

    private long DateToLong(String dateStr,String type) {
        SimpleDateFormat pattern = new SimpleDateFormat(type);
        Date date = null;
        try {
            date = pattern.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    private String LongToDate(long dateLong,String type) {
        SimpleDateFormat pattern = new SimpleDateFormat(type);
        return pattern.format(new Date(dateLong));
    }


    private LineDataSet getLineDataSet(List<Entry> data, String high,int color,String type) {
        LineDataSet dataSet = new LineDataSet(data, high);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawCircles(false);
        dataSet.setDrawFilled( true );//设置是否开启填充，默认为false
        dataSet.setFillColor(color);//设置填充颜色
        dataSet.setFillAlpha(60);//设置填充区域透明度，默认值为85
        dataSet.setColor(color);
        if (!type.equals("")){
            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return (int)value+type;
                }
            });
        }else {
            dataSet.setDrawValues(false);
        }
        return dataSet;
    }


}
