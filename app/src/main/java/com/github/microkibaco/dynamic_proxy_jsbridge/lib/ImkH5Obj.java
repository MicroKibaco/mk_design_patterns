package com.github.microkibaco.dynamic_proxy_jsbridge.lib;

import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 必须实现的接口
 */
public interface ImkH5Obj {

    void initEnv(WebView webView, AppCompatActivity activity);
}
