package com.alicetool.project.alicedesktop.Service;

import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class Location {
    public Context context;
    public LocationClient locationClient = null;

    public Location(Context context){
        this.context=context;
        locationClient=new LocationClient(context);

        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);

        option.setNeedNewVersionRgc(true);

        locationClient.setLocOption(option);
    }

    public void getLocation(BDAbstractLocationListener listener){
        locationClient.registerLocationListener(listener);
        locationClient.start();
    }

    public void close(){
        locationClient.stop();
    }

}
