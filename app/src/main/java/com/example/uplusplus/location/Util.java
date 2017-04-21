package com.example.uplusplus.location;

/**
 * Created by uplusplus on 2017/4/9.
 */
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Util {
    //log的标签
    public static final String TAG = "location";
    public static final boolean DEBUG = true;
    public static final String LOCATION_URL = "http://www.google.com/loc/json";
    public static final String LOCATION_HOST = "maps.google.com";

    public static void logi(String content){
        if(DEBUG) {
            Log.i(TAG, content);
        }
    }

    public static void loge(String content){
        if(DEBUG) {
            Log.e(TAG, content);
        }
    }

    public static void logd(String content){
        if(DEBUG) {
            Log.d(TAG, content);
        }
    }

    /**
     * 获取地理位置
     *
     * @throws Exception
     */
    public static String getLocation(String latitude, String longitude) throws Exception {
        String resultString = "";

        /** 这里采用get方法，直接将参数加到URL上 */
        String urlString = String.format("http://maps.google.cn/maps/geo?key=abcdefg&q=%s,%s", latitude, longitude);
        Util.logi("Util: getLocation: URL: " + urlString);

        /** 新建HttpClient */
        HttpClient client = new DefaultHttpClient();
        /** 采用GET方法 */
        HttpGet get = new HttpGet(urlString);
        try {
            /** 发起GET请求并获得返回数据 */
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
            StringBuffer strBuff = new StringBuffer();
            String result = null;
            while ((result = buffReader.readLine()) != null) {
                strBuff.append(result);
            }
            resultString = strBuff.toString();

            /** 解析JSON数据，获得物理地址 */
            if (resultString != null && resultString.length() > 0) {
                JSONObject jsonobject = new JSONObject(resultString);
                JSONArray jsonArray = new JSONArray(jsonobject.get("Placemark").toString());
                resultString = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    resultString = jsonArray.getJSONObject(i).getString("address");
                }
            }
        } catch (Exception e) {
            throw new Exception("获取物理位置出现错误:" + e.getMessage());
        } finally {
            get.abort();
            client = null;
        }

        return resultString;
    }
}