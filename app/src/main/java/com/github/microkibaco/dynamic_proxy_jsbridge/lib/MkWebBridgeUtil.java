package com.github.microkibaco.dynamic_proxy_jsbridge.lib;

import com.google.gson.Gson;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 杨正友(小木箱)于 2020/10/15 18 54 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkWebBridgeUtil {

    public static final String TAG = "WebDebugBridge";

    public static ImkBridgeHelper getHelper() {
        return helper;
    }

    private static ImkBridgeHelper helper;

    public static void init(ImkBridgeHelper helper){
        MkWebBridgeUtil.helper = helper;
    }



    public static void callback(final WebView webView, String funcName, MkResponse response){
        if (webView == null ) {
            Log.w(TAG,"webView == null,"+funcName);
            return;
        }

        if (TextUtils.isEmpty(funcName)) {
            Log.w(TAG,"TextUtils.isEmpty(funcName)");
            return;
        }
        String json = getHelper().toJson(response);
        Log.w(TAG,json);
        final String func = funcName+"("+json+")";


        //webView.getjsobj.getjsHandler.cancelTimeoutCountWhenFinish(finalCallbackName);
        // 执行JS调用
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:" + func);
                Log.i(TAG, "android call js back  "+func);
            }
        });
    }

    public static void callbackWithJson(final WebView webView, String funcName, String  json){
        if (webView == null ) {
            Log.w(TAG,"webView == null,"+funcName);
            return;
        }

        if (TextUtils.isEmpty(funcName)) {
            Log.w(TAG,"TextUtils.isEmpty(funcName)");
            return;
        }
        Log.w(TAG,json);
        final String func = funcName+"("+json+")";


        //webView.getjsobj.getjsHandler.cancelTimeoutCountWhenFinish(finalCallbackName);
        // 执行JS调用
        webView.post(() -> {
            webView.loadUrl("javascript:" + func);
            Log.i(TAG, "android call js back  "+func);
        });
    }

    public static void callbackWithJson(final WebView webView, String funcName, MkResponse response){
        callbackWithJson(webView, funcName, new Gson().toJson(response));
    }



    public static JSONObject getJson(String paramsJson) throws JSONException {
        return new JSONObject(paramsJson);
    }



}
