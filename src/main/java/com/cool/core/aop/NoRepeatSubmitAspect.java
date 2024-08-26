package com.cool.core.aop;

import com.cool.core.annotation.NoRepeatSubmit;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.lock.CoolLock;
import com.cool.core.util.CoolSecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class NoRepeatSubmitAspect {

    private final CoolLock coolLock;

    @Around("@annotation(noRepeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint, NoRepeatSubmit noRepeatSubmit) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
            RequestContextHolder.getRequestAttributes())).getRequest();
        String key = request.getRequestURI() + ":" + CoolSecurityUtil.getCurrentUserId();
        // 加锁
        CoolPreconditions.check(!coolLock.tryLock(key, Duration.ofMillis(noRepeatSubmit.expireTime())), "请勿重复操作");
        try {
            return joinPoint.proceed();
        } finally {
            // 移除锁
            coolLock.unlock(key);
        }
    }
}
