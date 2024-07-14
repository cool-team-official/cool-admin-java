package com.cool.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常处理
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CoolException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;
    private Object data;

    public CoolException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public CoolException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public CoolException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public CoolException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public CoolException(Object data) {
        this.data = data;
    }
}
