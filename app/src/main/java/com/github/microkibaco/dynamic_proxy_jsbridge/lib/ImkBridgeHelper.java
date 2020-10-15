package com.github.microkibaco.dynamic_proxy_jsbridge.lib;

import android.app.Activity;

/**
 * @author 杨正友(小木箱)于 2020/10/15 18 48 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public interface ImkBridgeHelper {

    String toJson(Object o);

    void runOnMain(Runnable runnable, int timeout);

    Activity getTopActivity();

    void reportException(Throwable e);
}