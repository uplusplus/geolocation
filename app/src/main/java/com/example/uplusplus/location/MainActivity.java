package com.example.uplusplus.location;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    TextView mTv_wifi, mTv_geo;
    Button mButton;
    Button mbtn_cpy;
    Button mbtn_hack;
    WifiLocationManager wifiLocation;
    CellLocationManager cellLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTv_wifi = (TextView) findViewById(R.id.tv_wifi_info);
        mTv_geo = (TextView) findViewById(R.id.tv_geolocation);
        mButton = (Button) findViewById(R.id.btn_update);
        mbtn_cpy = (Button) findViewById(R.id.btn_copy);
        mbtn_hack = (Button) findViewById(R.id.btn_hack);
//        wifiLocation = new WifiLocationManager(MainActivity.this);
        wifiLocation = new WifiLocationManager(getApplicationContext());
        cellLocation = new CellLocationManager(MainActivity.this);

        ClassLoader cl = getClassLoader();
        ClassLoader cl1 = ClassLoader.getSystemClassLoader();
        this.getDir("dex", 0).getAbsolutePath();
        mButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Util.logi("MainActivity: on mButton Click!!!");
                getLocation();
            }

        });

        mbtn_cpy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

                ClipData myClip;
                String text = mTv_wifi.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(), "复制成功，进微信直接粘贴发送。", Toast.LENGTH_LONG).show();
            }
        });

        mbtn_hack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFake();
            }
        });
        mTv_wifi.setText("扫描步骤：\n1. 连接公司wifi\n2. 点击启动扫描按钮,扫描结果会呈现在此处。\n3. 点出复制按钮，结果会保存到剪切板，可粘贴发送到微信。\n");
    }

    public void doFake(){
        wifiLocation.setCurrrentWifi("PingAn-LifeAgent", "04:bd:88:ed:cf:a0", "80:71:7a:17:a8:91");
        wifiLocation.setNetworkInfo(ConnectivityManager.TYPE_WIFI, 0, "WIFI", "", "PingAn-LifeAgent");
        String[][] result = {
                {"04:bd:88:ed:cf:b0","PingAn-LifeAgent","-76"},
                {"88:25:93:21:35:62","TP-LINK光辉部","-59"},
                {"04:bd:88:ed:cf:a0","PingAn-LifeAgent","-63"},
                {"84:d4:7e:56:51:c0","PingAn-LifeAgent","-79"},
                {"80:89:17:a7:56:97","TP-LINK_5697","-93"},
                {"84:d4:7e:56:47:00","PingAn-LifeAgent","-63"},
                {"88:25:93:2b:e4:08","dfmj00","-77"},
                {"04:bd:88:ee:bd:e0","PingAn-LifeAgent","-73"},
                {"b0:95:8e:12:02:16","TP-LINK_0216","-85"},
                {"b2:95:8e:12:02:16","TPGuest_0216","-86"},
                {"88:25:93:43:b8:44","dfmj02","-93"},
                {"88:25:93:43:b8:14","dfmj01","-83"},
                {"84:d4:7e:56:47:10","PingAn-LifeAgent","-78"},
                {"04:bd:88:ee:bd:f0","PingAn-LifeAgent","-77"},
                {"b0:95:8e:12:02:18","TP-LINK_5G_0216","-90"},
                {"78:d3:8d:be:84:74","","-88"},
                {"8c:be:be:27:eb:dd","Xiaomi_tq","-87"},
                {"50:bd:5f:67:6f:6a","TP-LINK_6F6A","-89"},
                {"78:d3:8d:be:84:90","","-84"},
                {"24:69:68:d4:28:c0","SASA","-91"},
                {"fc:d7:33:71:2f:0c","cxs","-96"},
        };
        wifiLocation.setScanResults(result);
    }

    private void getLocation(){

//        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
//        ConnectivityManager connectivityManager1 = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isNet = wifiLocation.isNetworkAvaliable();
        mTv_wifi.setText("当前网络连接信息：\n" + wifiLocation.getCurrrentNetworkInfo() + "\n\n");
        mTv_wifi.append(wifiLocation.getWifiInfo() + "\n");

        if(!isNet){
            mTv_wifi.append("No network.\n");
            Toast.makeText(MainActivity.this, "网络不可用：打开WIFI 或 数据连接！！！", Toast.LENGTH_LONG).show();
            Util.logd("MainActivity: getLocation: Net work is not avaliable, and return!!!");
            return;
        }
        boolean isWifi = wifiLocation.isWifiNetwrokType();
        if(isWifi){
            Util.logd("MainActivity: getLocation: Wifi定位");
            mTv_wifi.append(wifiLocation.getCurrentWifi() + "\n");
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
