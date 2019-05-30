package com.sqh.market.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 基础网络请求工具类
 *
 * @author 郑龙
 */
public class NetUtil {
    /**
     * 单例化http客户端
     */
    private static OkHttpClient mClient = new OkHttpClient();

    /**
     * 向指定地址发送get请求
     *
     * @param url      目标地址
     * @param callback 自定义回调函数
     */
    public static void doGet(String url, Callback callback) {
        Log.d("GET Url", url);
        Request request = new Request.Builder().url(url).get().build();
        mClient.newCall(request).enqueue(callback);
    }

    /**
     * 向指定地址发送post请求
     *
     * @param url      目标地址
     * @param body     请求体
     * @param callback 自定义回调函数
     */
    public static void doPost(String url, RequestBody body, Callback callback) {
        Log.d("POST Url", url);
        Request request = new Request.Builder().url(url).post(body).build();
        mClient.newCall(request).enqueue(callback);
    }

    /**
     * 检测网络是否可用
     *
     * @param context content
     * @return true/false  可用/不可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
}
