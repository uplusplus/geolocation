package com.example.uplusplus.location;

/**
 * Created by uplusplus on 2017/4/9.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class WifiLocationManager {

    private Context mContext;
    private WifiManager wifiManager;

    public WifiLocationManager(Context context){
        mContext = context;
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    public void getLocation(BroadcastReceiver receiver){
        mContext.registerReceiver(receiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    public List<ScanResult> getWifiList(){
        return wifiManager.getScanResults();
    }
/*
    bssid=08:10:77:f9:2a:1b;freq=2472;level=-44;flags=43;ssid=Netcore_2_4G;
    bssid=94:77:2b:27:e5:0c;freq=2412;level=-69;flags=11;ssid=lady_x;
    bssid=ec:26:ca:a5:f4:65;freq=2462;level=-75;flags=11;ssid=swj1993;
    bssid=ec:26:ca:34:64:90;freq=2462;level=-84;flags=11;ssid=TP-LINK88888;
    bssid=8e:25:93:ac:91:fe;freq=2412;level=-91;flags=11;ssid=dongruitianyou2;

     holder.put: {
     "version":"1.1.0",
     "host":"maps.google.com",
     "address_language":"zh_CN",
     "request_address":true,
     "wifi_towers":[
        {"mac_address":"08:10:77:f9:2a:1b","ssid":"Netcore_2_4G","signal_strength":-42},
        {"mac_address":"94:77:2b:27:e5:0c","ssid":"lady_x","signal_strength":-70},
        {"mac_address":"ec:26:ca:a5:f4:65","ssid":"swj1993","signal_strength":-75},
        {"mac_address":"ec:26:ca:34:64:90","ssid":"TP-LINK88888","signal_strength":-69},
        {"mac_address":"c0:61:18:92:f8:a6","ssid":"TP-LINK_F8A6","signal_strength":-85}]}
* */

    public String getWifiInfo(){
            List<ScanResult> wifiList = getWifiList();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < wifiList.size(); i++) {
                JSONObject tower = new JSONObject();
                try {
                    tower.put("mac_address", wifiList.get(i).BSSID);
                    tower.put("ssid", wifiList.get(i).SSID);
                    tower.put("signal_strength", wifiList.get(i).level);
                    sb.append(tower.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        return sb.toString();
    }
    public String getLocationWifi(){

        /** 采用Android默认的HttpClient */
        HttpClient client = new DefaultHttpClient();
        /** 采用POST方法 */
        HttpPost post = new HttpPost(Util.LOCATION_URL);
        try {
            /** 构造POST的JSON数据 */
            JSONObject holder = new JSONObject();
            holder.put("version", "1.1.0");
            holder.put("host", Util.LOCATION_HOST);
            holder.put("address_language", "zh_CN");
            holder.put("request_address", true);

            JSONArray towerarray = new JSONArray();
            List<ScanResult> wifiList = getWifiList();
            for (int i = 0; i < wifiList.size(); i++) {
                JSONObject tower = new JSONObject();
                tower.put("mac_address", wifiList.get(i).BSSID);
                tower.put("ssid", wifiList.get(i).SSID);
                tower.put("signal_strength", wifiList.get(i).level);
                towerarray.put(tower);
            }
            holder.put("wifi_towers", towerarray);
            Util.logd("holder.put: " + holder.toString());

            StringEntity query = new StringEntity(holder.toString());
            post.setEntity(query);

            /** 发出POST数据并获取返回数据 */
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
            StringBuffer strBuff = new StringBuffer();
            String result = null;
            while ((result = buffReader.readLine()) != null) {
                strBuff.append(result);
            }
            Util.logd("result: " + strBuff.toString());

            /** 解析返回的JSON数据获得经纬度 */
            JSONObject json = new JSONObject(strBuff.toString());
            JSONObject subjosn = new JSONObject(json.getString("location"));

            String latitude = subjosn.getString("latitude");
            String longitude = subjosn.getString("longitude");

            return Util.getLocation(latitude, longitude);

        } catch(ClientProtocolException e){
            Util.loge("ClientProtocolException : " + e.getMessage());
        }catch(IOException e){
            Util.loge("IOException : " + e.getMessage());
        } catch (Exception e) {
            Util.loge("Exception : " + e.getMessage());
        } finally{
            post.abort();
            client = null;
        }
        return null;
    }
}