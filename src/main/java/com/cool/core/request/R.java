package com.cool.core.request;

import java.util.HashMap;

/**
 * 返回信息
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R() {
        put("code", 1000);
        put("message", "success");
    }

    public static R error() {
        return error(1001, "请求方式不正确或服务出现异常");
    }

    public static R error(String msg) {
        return error(1001, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("message", msg);
        return r;
    }

    public static R okMsg(String msg) {
        R r = new R();
        r.put("message", msg);
        return r;
    }

    public static R ok() {
        return new R();
    }

    public static R ok(Object data) {
        return new R().put("data", data);
    }

    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}