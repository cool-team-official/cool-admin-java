package com.cool.core.security;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.cool.core.cache.CoolCache;
import com.cool.core.enums.UserTypeEnum;
import com.cool.core.security.jwt.JwtTokenUtil;
import com.cool.core.security.jwt.JwtUser;
import com.cool.core.util.PathUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Token过滤器
 */
@Order(1)
@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    final private JwtTokenUtil jwtTokenUtil;
    final private CoolCache coolCache;
    final private IgnoredUrlsProperties ignoredUrlsProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (PathUtils.isMatch(ignoredUrlsProperties.getAdminAuthUrls(), requestURI)) {
            // 请求路径在忽略后台鉴权url里支持通配符，放行
            chain.doFilter(request, response);
            return;
        }
        String authToken = request.getHeader("Authorization");
        if (!StrUtil.isEmpty(authToken)) {
            JWT jwt = jwtTokenUtil.getTokenInfo(authToken);

            Object userType = jwt.getPayload("userType");
            if (Objects.equals(userType, UserTypeEnum.APP.name())) {
                // app
                handlerAppRequest(request, jwt, authToken);
            } else {
                // admin
                handlerAdminRequest(request, jwt, authToken);
            }
        }
        chain.doFilter(request, response);
    }
    /**
     * 处理app请求
     */
    private void handlerAppRequest(HttpServletRequest request, JWT jwt, String authToken) {
        String userId = jwt.getPayload("userId").toString();
        if (ObjectUtil.isNotEmpty(userId)
            && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = coolCache.get("app:userDetails:" + userId,
                JwtUser.class);
            if (jwtTokenUtil.validateToken(authToken) && userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("userId", jwt.getPayload("userId"));
                request.setAttribute("tokenInfo", jwt);
            }
        }
    }

    /**
     * 处理后台请求
     */
    private void handlerAdminRequest(HttpServletRequest request, JWT jwt, String authToken) {
        String username = jwt.getPayload("username").toString();
        if (username != null
            && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = coolCache.get("admin:userDetails:" + username,
                JwtUser.class);
            Integer passwordV = Convert.toInt(jwt.getPayload("passwordVersion"));
            Integer rv = coolCache.get("admin:passwordVersion:" + jwt.getPayload("userId"),
                Integer.class);
            if (jwtTokenUtil.validateToken(authToken, username) && Objects.equals(passwordV, rv)
                && userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("adminUsername", jwt.getPayload("username"));
                request.setAttribute("adminUserId", jwt.getPayload("userId"));
                request.setAttribute("tokenInfo", jwt);
            }
        }
    }
}
