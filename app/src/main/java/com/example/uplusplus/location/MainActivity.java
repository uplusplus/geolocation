package com.example.uplusplus.location;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    TextView mTv_wifi, mTv_geo;
    Button mButton;
    WifiLocationManager wifiLocation;
    CellLocationManager cellLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTv_wifi = (TextView) findViewById(R.id.tv_wifi_info);
        mTv_geo = (TextView) findViewById(R.id.tv_geolocation);
        mButton = (Button) findViewById(R.id.btn_update);
        wifiLocation = new WifiLocationManager(MainActivity.this);
        cellLocation = new CellLocationManager(MainActivity.this);

        mButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Util.logi("MainActivity: on mButton Click!!!");
                getLocation();
            }

        });
    }

    private void getLocation(){
        boolean isNet = Util.isNetworkAvaliable(MainActivity.this);
        if(!isNet){
            mTv_wifi.append("No network.\n");
            Toast.makeText(MainActivity.this, "网络不可用：打开WIFI 或 数据连接！！！", Toast.LENGTH_LONG).show();
            Util.logd("MainActivity: getLocation: Net work is not avaliable, and return!!!");
            return;
        }
        boolean isWifi = Util.isWifiNetwrokType(MainActivity.this);
        if(isWifi){
            mTv_wifi.append("wifi location\n");
            Util.logd("MainActivity: getLocation: Wifi定位");
            mTv_wifi.append(wifiLocation.getWifiInfo());
            wifiLocation.getLocation(new WifiReceiver());
        }else{
            mTv_wifi.append("cell location\n");
            Util.logd("MainActivity: getLocation: 基站定位");
            String location = cellLocation.getLocationCell();
            mTv_geo.setText(location);
        }
    }
    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            Util.logi("get broadcastReceiver: SCAN_RESULTS_AVAILABLE_ACTION");
            String location = wifiLocation.getLocationWifi();
            mTv_geo.setText(location);
        }
    }
}
