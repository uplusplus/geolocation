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
    TextView mTextView;
    Button mButton;
    WifiLocationManager wifiLocation;
    CellLocationManager cellLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.tv_show_info);
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
            Toast.makeText(MainActivity.this, "网络不可用：打开WIFI 或 数据连接！！！", Toast.LENGTH_LONG).show();
            Util.logd("MainActivity: getLocation: Net work is not avaliable, and return!!!");
            return;
        }
        boolean isWifi = Util.isWifiNetwrokType(MainActivity.this);
        if(isWifi){
            Util.logd("MainActivity: getLocation: Wifi定位");
            wifiLocation.getLocation(new WifiReceiver());
        }else{
            Util.logd("MainActivity: getLocation: 基站定位");
            String location = cellLocation.getLocationCell();
            mTextView.setText(location);
        }
    }
    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            Util.logi("get broadcastReceiver: SCAN_RESULTS_AVAILABLE_ACTION");
            String location = wifiLocation.getLocationWifi();
            mTextView.setText(location);
        }
    }
}
