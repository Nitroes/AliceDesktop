package com.alicetool.project.alicedesktop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.alicetool.project.alicedesktop.Service.Location;
import com.alicetool.project.alicedesktop.Service.SQLHelper;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView=findViewById(R.id.textView);

        SQLHelper sqlHelper=SQLHelper.getInit(getApplicationContext());
        textView.setText(sqlHelper.getString("adcode"));

        findViewById(R.id.button).setOnClickListener(v->{
//            Location location=new Location(getApplicationContext());
//            location.getLocation(new BDAbstractLocationListener() {
//                @Override
//                public void onReceiveLocation(BDLocation bdLocation) {
//                    String adcode = bdLocation.getAdCode();    //获取adcode
//                    SQLHelper sqlHelper=SQLHelper.getInit(getApplicationContext());
//                    sqlHelper.putString("adcode",adcode);
//                    textView.setText(adcode);
//                    location.close();
//                }
//            });
            try {
                textView.setText(sqlHelper.getBeforeWeather(new String[]{"temp"}).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}
