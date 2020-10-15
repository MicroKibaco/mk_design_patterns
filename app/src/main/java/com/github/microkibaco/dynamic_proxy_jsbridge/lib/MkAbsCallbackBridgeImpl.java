package com.github.microkibaco.dynamic_proxy_jsbridge.lib;

import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * by yangzy
 * data:2020-09-25
 * desc: 基类,指定生成带webview的构造函数
 */
public class MkAbsCallbackBridgeImpl {
    protected WebView webView;
    protected AppCompatActivity activity;

    public MkAbsCallbackBridgeImpl(WebView webView, AppCompatActivity activity) {
        this.webView = webView;
        this.activity = activity;
        if (activity == null) {
            if (MkWebBridgeUtil.getHelper().getTopActivity() instanceof AppCompatActivity) {
                this.activity = (AppCompatActivity) MkWebBridgeUtil.getHelper().getTopActivity();
            }
        }
    }

    /**
     * 获取当前Activity
     * @return 当前Activity
     */
    protected AppCompatActivity getCurrentActivity() {
        if (MkWebBridgeUtil.getHelper().getTopActivity() instanceof AppCompatActivity) {
            return (AppCompatActivity) MkWebBridgeUtil.getHelper().getTopActivity();
        }
        return null;
    }


}
