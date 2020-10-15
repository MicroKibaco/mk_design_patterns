package com.github.microkibaco.dynamic_proxy_jsbridge.lib;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 杨正友(小木箱)于 2020/10/15 18 49 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description: 接口响应报文
 */
public class MkResponse<T> {

    public T data;
    public boolean success;
    public String errCode;
    public String errMsg;

    /**
     * 请求失败Error TAG
     */
    public final static String CODE_CANCEL = "取消请求";
    public final static String CODE_TIMEOUT = "超时";
    public final static String CODE_JAVA_EXCEPTION = "Java异常";
    public final static String CODE_ILLIGAL_PARAMS = "非法参数";

    public final static String CODE_EMPTY = "空";
    public final static String CODE_UN_LOGIN = "未登录";


    /**
     * 请求成功回调
     * @param data 数据源
     * @param <T> 泛型实体
     * @return MkResponse
     */
    public static <T> MkResponse<T> success(T data) {
        MkResponse<T> response = new MkResponse<T>();
        response.success = true;
        response.data = data;
        return response;
    }

    /**
     * 将数据添加到实体对象
     * @param key 键
     * @param value 实体对象
     * @return MkResponse.Builder
     */
    public static MkResponse.Builder put(String key, Object value) {
        return MkResponse.newBuilder().put(key, value);
    }

    /**
     * 构建成功回调
     * @return 实体数据
     */
    public static MkResponse.Builder success() {
        return new MkResponse.Builder();
    }

    /**
     * error 回调
     * @param code 回调码
     * @param msg 回调信息
     * @return MkResponse
     */
    public static MkResponse error(String code, String msg) {
        MkResponse response = new MkResponse();
        response.success = false;
        response.errCode = code;
        response.errMsg = msg;
        return response;
    }

    @Override
    public String toString() {
        return "MkResponse{" +
                "data=" + data +
                ", success=" + success +
                ", errCode='" + errCode + '\'' +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }

    /**
     * 回调取消
     * @return
     */
    public static MkResponse cancel() {
        return error(CODE_CANCEL, "");
    }

    /**
     * 回调信息为空
     * @return MkResponse
     */
    public static MkResponse empty() {
        return error(CODE_EMPTY, "数据为空");
    }

    /**
     * 未登录
     * @return MkResponse
     */
    public static MkResponse unLogin() {
        return error(CODE_UN_LOGIN, "需要继续登录");
    }

    /**
     * 超时
     * @param time 超时时间
     * @return MkResponse
     */
    public static MkResponse timeout(int time) {
        return error(CODE_TIMEOUT, time + "超时");
    }

    /**
     * 异常信息
     * @param throwable 未上报异常
     * @return MkResponse
     */
    public static MkResponse exception(Throwable throwable) {
        if (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return error(CODE_JAVA_EXCEPTION, throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
    }

    /**
     * 非法参数
     * @param params 参数类型
     * @return 非法参数
     */
    public static MkResponse illegalParams(String params) {
        return error(CODE_ILLIGAL_PARAMS, params);
    }

    /**
     * 构建者类型
     * @return
     */
    public static Builder newBuilder() {
        return new Builder();
    }


    /**
     * 构建者设计模式
     */
    public static final class Builder {
        private final Map<String, Object> data = new HashMap<>(18);

        private Builder() {
        }

        public Builder put(String key, Object value) {
            data.put(key, value);
            return this;
        }

        public MkResponse build() {
            return MkResponse.success(data);
        }
    }
}
