package com.cool.core.exception;

import cn.hutool.core.util.ObjUtil;
import com.cool.core.request.R;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 */
@RestControllerAdvice
@Slf4j
public class CoolExceptionHandler {

    @ExceptionHandler(CoolException.class)
    public R handleRRException(CoolException e) {
        R r = new R();
        if (ObjUtil.isNotEmpty(e.getData())) {
            r.setData( e.getData() );
        } else {
            r.setCode( e.getCode() );
            r.setMessage( e.getMessage() );
        }
        if (ObjUtil.isNotEmpty(e.getCause())) {
            log.error(e.getCause().getMessage(), e.getCause());
        }
        return r;
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public R handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return R.error("已存在该记录或值不能重复");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public R handleBadCredentialsException(BadCredentialsException e) {
        log.error(e.getMessage(), e);
        return R.error("账户密码不正确");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return R.error("不支持该请求方式，请区分POST、GET等请求方式是否正确");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public R handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return R.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        log.error(e.getMessage(), e);
        return R.error();
    }

    @ExceptionHandler(WxErrorException.class)
    public R handleException(WxErrorException e) {
        log.error(e.getMessage(), e);
        return R.error(e.getMessage());
    }
}
