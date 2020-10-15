package com.github.microkibaco.dynamic_proxy_jsbridge.lib.interfc;

import android.app.Activity;

/**
 * @author 杨正友(小木箱)于 2020/10/15 18 48 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public interface ImkBridgeHelper {

    /**
     * 转 JSON 字符串
     */
    String toJson(Object o);

    /**
     * 异常上报
     * @param e Throwable
     */
    void reportException(Throwable e);

    /**
     * 运行在主线程
     * @param runnable runnable 回调
     * @param timeout 超时时间
     */
    void runOnMain(Runnable runnable, int timeout);

    /**
     * 获取顶部Activity
     */
    Activity getTopActivity();

}