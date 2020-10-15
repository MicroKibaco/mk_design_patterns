package com.github.microkibaco.dynamic_proxy_jsbridge.lib;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 杨正友(小木箱)于 2020/10/15 18 49 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkResponse<T>  {

    public T data;
    public boolean success;
    public String errCode;
    public String errMsg;
    public int sysTime = (int) System.currentTimeMillis();


    public final static String CODE_CANCEL = "cancel";
    public final static String CODE_TIMEOUT = "time out";
    public final static String CODE_JAVA_EXCEPTION = "java exception";
    public final static String CODE_ILLIGAL_PARAMS = "illeagal params";

    public final static String CODE_EMPTY = "empty";
    public final static String CODE_UN_LOGIN = "unLogin";


    public static <T> MkResponse<T> success(T data){
        MkResponse<T> response = new MkResponse<T>();
        response.success = true;
        response.data = data;
        return response;
    }




    public static MkResponse.Builder put(String key,Object value){

        return MkResponse.newBuilder().put(key,value);
    }

    public static  MkResponse.Builder  success(){
        return new MkResponse.Builder();
    }

    public static MkResponse error(String code, String msg){
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

    public static MkResponse cancel(){
        return error(CODE_CANCEL,"");
    }

    public static MkResponse empty(){
        return error(CODE_EMPTY,"data is empty");
    }
    public static MkResponse unlogin(){
        return error(CODE_UN_LOGIN,"need login to continue");
    }

    public static MkResponse timeout(int time){
        return error(CODE_TIMEOUT,time+"");
    }
    public static MkResponse exception(Throwable throwable){
        if(throwable.getCause() != null){
            throwable = throwable.getCause();
        }
        return error(CODE_JAVA_EXCEPTION,throwable.getClass().getSimpleName()+": "+throwable.getMessage());
    }
    public static MkResponse illeagalParms(String params){
        return error(CODE_ILLIGAL_PARAMS,params);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    /**
     * 构建者设计模式
     */
    public static final class Builder {
        private Map<String, Object> data = new HashMap();

        private Builder() {
        }

        public Builder put(String key,Object value) {
            data .put(key, value);
            return this;
        }

        public MkResponse build() {
            return MkResponse.success(data);
        }
    }
}
