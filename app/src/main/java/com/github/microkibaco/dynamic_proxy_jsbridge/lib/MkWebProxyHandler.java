package com.github.microkibaco.dynamic_proxy_jsbridge.lib;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * @author 杨正友(小木箱)于 2020/10/15 19 26 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkWebProxyHandler<T> implements InvocationHandler {
    public static final String TAG = "WebDebugBridge";
    private T target;
    private WebView webView;
    private boolean runOnMainThread = true;

    public MkWebProxyHandler<T> setNotRunOnMainThread(boolean notRunOnMainThread) {
        this.runOnMainThread = !notRunOnMainThread;
        return this;
    }


    public MkWebProxyHandler(T target, WebView webView) {
        this.target = target;
        this.webView = webView;
    }


    public T getProxy() {

        try {
            Class<T> cls = (Class<T>) target.getClass();
            // 查看目标类有没有实现接口
            if (cls.getInterfaces() != null) {
                return (T) newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), this);
            }
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }

        return target;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        //入参打印
        Log.d(TAG, String.format("method name: %s, annotation: %s, args: %s", method.getName(),
                Arrays.toString(method.getDeclaredAnnotations()), Arrays.toString(args)));
        final long startTime = System.nanoTime();

        final Object[] retVal = {null};
        String callbackName = "";
        String jsonStr = "";
        try {
            if (args != null) {
                if (args.length > 0) {
                    jsonStr = args[0] + "";
                }
                if (args.length >= 2) {
                    callbackName = String.valueOf(args[1]);
                }
            }
            try {
                //json格式校验
                JSONObject jsonObject = new JSONObject(jsonStr);
            } catch (Throwable throwable) {
                Log.getStackTraceString(throwable);
                MkWebBridgeUtil.callback(webView, callbackName, MkResponse.illegalParams(jsonStr));
                return null;
            }

            if (runOnMainThread) {
                final String finalCallbackName1 = callbackName;
                MkWebBridgeUtil.getHelper().runOnMain(() -> {
                    try {
                        //真正调用. 确保到了jsHandler里的json参数都是没有问题的
                        retVal[0] = method.invoke(target, args);
                    } catch (Throwable e) {
                        Log.getStackTraceString(e);
                        MkWebBridgeUtil.callback(webView, finalCallbackName1, MkResponse.exception(e));
                        MkWebBridgeUtil.getHelper().reportException(e);
                        if (TextUtils.isEmpty(finalCallbackName1)) {
                            Log.d(TAG, "finalCallbackName1 is null");
                        }
                    } finally {
                        //出参打印
                        long cost = System.nanoTime() - startTime;
                        Log.d(TAG, cost > 1000000 ?
                                String.format("end method name: %s, time cost %sms, return value: %s", method.getName(), cost / 1000000, retVal[0] == null ? "null" : retVal[0].toString()) :
                                String.format("end method name: %s, time cost %sus, return value: %s", method.getName(), cost / 1000, retVal[0] == null ? "null" : retVal[0].toString()));
                    }

                }, 0);
            } else {
                //真正调用. 确保到了jsHandler里的json参数都是没有问题的
                retVal[0] = method.invoke(target, args);
            }
        } catch (Throwable e) {
            //防止调用时crash,导致页面web页面卡住不动
            Log.getStackTraceString(e);
            MkWebBridgeUtil.callback(webView, callbackName, MkResponse.exception(e));
            MkWebBridgeUtil.getHelper().reportException(e);
            if (TextUtils.isEmpty(callbackName)) {
                Log.d(TAG, "callbackName is null");

            }
        } finally {
            if (!runOnMainThread) {
                //出参打印

                 long cost = System.nanoTime() - startTime;
                if (cost > 1000000) {
                    Log.d(TAG, String.format("end method name: %s, time cost %sms, return value: %s",
                            method.getName(), cost / 1000000, retVal[0] == null ? "null" : retVal[0].toString()));
                } else {
                    Log.d(TAG, String.format("end method name: %s, time cost %sus, return value: %s",
                            method.getName(), cost / 1000, retVal[0] == null ? "null" : retVal[0].toString()));
                }
            }

        }
        return retVal[0];
    }
}
