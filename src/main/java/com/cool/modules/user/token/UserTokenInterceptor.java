package com.cool.modules.user.token;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.security.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户Token拦截器
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class UserTokenInterceptor implements HandlerInterceptor {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查是否有 TokenIgnore 注解，有则跳过
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.getMethodAnnotation(TokenIgnore.class) != null ||
                    handlerMethod.getBeanType().getAnnotation(TokenIgnore.class) != null) {
                return true;
            }
        }
        String token = request.getHeader("Authorization");
        if (StrUtil.isNotEmpty(token)) {
            try {
                if (jwtTokenUtil.validateToken(token)) {
                    JWT jwt = jwtTokenUtil.getTokenInfo(token);
                    String userId = jwt.getPayload("userId").toString();
                    request.setAttribute("appUserId", userId);
                    return true;
                }
            } catch (Exception e) {
                // Logging can be added here if needed
                log.error("Invalid Token", e);
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid Token");
        return false;
    }
}
