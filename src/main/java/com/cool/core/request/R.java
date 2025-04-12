package com.cool.core.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 返回信息
 */
@Schema(title = "响应数据结构")
@Data
public class R<T>  implements Serializable {
    private static final long serialVersionUID = 1L;


    @Schema(title = "编码：1000表示成功，其他值表示失败")
    private int code = 1000;

    @Schema(title = "消息内容")
    private String message = "success";

    @Schema(title = "响应数据")
    private T data;
    
    @Schema(title = "响应数据")
    private Map<String, Object> dataMap = new HashMap<String, Object>();
    
    
    public R() {
        
    }    
    
    public R( int code, String message, T data ) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static R error() {
        return error(1001, "请求方式不正确或服务出现异常");
    }

    public static R error(String msg) {
        return error(1001, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.code = code;
        r.message = msg;
        return r;
    }

    public static R okMsg(String msg) {
        R r = new R();
        r.message = msg;
        return r;
    }

    public static R ok() {
        return new R();
    }

    public static <B> R<B> ok(B data) {
        return new R<B>(1000 , "ok", data);
    }


    public R<T> put(String key, Object value) {
        if ( key.equals( "code") ) {
            this.code = (int)value;
        } else if ( key.equals( "message") ) {
            this.message = (String)value;
        } else if ( key.equals( "data") ) {
            this.data = (T) value;
        } else {
            dataMap.put(key, value);
        }
        return this;
    }
}