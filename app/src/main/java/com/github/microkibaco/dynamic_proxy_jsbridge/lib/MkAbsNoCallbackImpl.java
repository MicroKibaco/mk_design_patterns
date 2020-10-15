package com.github.microkibaco.dynamic_proxy_jsbridge.lib;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author 杨正友(小木箱)于 2020/10/15 18 53 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkAbsNoCallbackImpl {

    protected AppCompatActivity activity;

    /**
     * 没有回调的AppCompatActivity 实现
     * @param activity activity上下文
     */
    public MkAbsNoCallbackImpl(AppCompatActivity activity) {
        this.activity = activity;
        if(activity == null){
            // rn可以传null,自动获取上层activity
            if(MkWebBridgeUtil.getHelper().getTopActivity() instanceof AppCompatActivity){
                this.activity = (AppCompatActivity) MkWebBridgeUtil.getHelper().getTopActivity();
            }
        }
    }


}