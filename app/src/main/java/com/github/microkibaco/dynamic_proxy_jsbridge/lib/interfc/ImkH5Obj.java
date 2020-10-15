package com.github.microkibaco.dynamic_proxy_jsbridge.lib.interfc;


import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * by  yangzy data:2020-09-26 desc: 必须实现的接口
 */
public interface ImkH5Obj {

    void initEnv(WebView webView, AppCompatActivity activity);
}
