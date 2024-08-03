package com.cool.core.security.jwt;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import com.cool.core.config.CoolProperties;
import com.cool.modules.base.service.sys.BaseSysConfService;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * JWT工具类
 */
@Component
@RequiredArgsConstructor
public class JwtTokenUtil implements Serializable {

    final private CoolProperties coolProperties;
    final private BaseSysConfService baseSysConfService;
    final String tokenKey = "JWT_SECRET_TOKEN";
    final String refreshTokenKey = "JWT_SECRET_REFRESH_TOKEN";

    public long getExpire() {
        return this.coolProperties.getToken().getExpire();
    }

    public long getRefreshExpire() {
        return this.coolProperties.getToken().getRefreshExpire();
    }

    public String getTokenSecret() {
        String secret = baseSysConfService.getValueWithCache(tokenKey);
        if (StrUtil.isBlank(secret)) {
            secret = StrUtil.uuid().replaceAll("-", "");
            baseSysConfService.setValue(tokenKey, secret);
        }
        return secret;
    }

    public String getRefreshTokenSecret() {
        String secret = baseSysConfService.getValueWithCache(refreshTokenKey);
        if (StrUtil.isBlank(secret)) {
            secret = StrUtil.uuid().replaceAll("-", "");
            baseSysConfService.setValue(refreshTokenKey, secret);
        }
        return secret;
    }

    /**
     * 生成令牌
     *
     * @param tokenInfo 保存的用户信息
     * @return 令牌
     */
    public String generateToken(Map<String, Object> tokenInfo) {
        tokenInfo.put("isRefresh", false);
        Date expirationDate = new Date(System.currentTimeMillis() + getExpire() * 1000);
        JWT jwt = JWT.create().setExpiresAt(expirationDate).setKey(getTokenSecret().getBytes())
                .setPayload("created", new Date());
        tokenInfo.forEach(jwt::setPayload);
        return jwt.sign();
    }

    /**
     * 生成令牌
     *
     * @param tokenInfo 保存的用户信息
     * @return 令牌
     */
    public String generateRefreshToken(Map<String, Object> tokenInfo) {
        tokenInfo.put("isRefresh", true);
        Date expirationDate = new Date(System.currentTimeMillis() + getRefreshExpire() * 1000);
        JWT jwt = JWT.create().setExpiresAt(expirationDate).setKey(getRefreshTokenSecret().getBytes())
                .setPayload("created", new Date());
        tokenInfo.forEach(jwt::setPayload);
        return jwt.sign();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        JWT jwt = JWT.of(token);
        return jwt.getPayload("username").toString();
    }

    /**
     * 获得token信息
     *
     * @param token 令牌
     * @return token信息
     */
    public JWT getTokenInfo(String token) {
        return JWT.of(token);
    }

    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            JWTValidator.of(token).validateDate(DateUtil.date());
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证令牌
     *
     * @param token    令牌
     * @param username 用户
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        if (ObjectUtil.isEmpty(token)) {
            return false;
        }
        String tokenUsername = getUsernameFromToken(token);
        String secret = getTokenSecret();
        boolean isValidSignature = JWTUtil.verify(token, secret.getBytes());
        return (tokenUsername.equals(username) && !isTokenExpired(token) && isValidSignature);
    }

    /**
     * 校验token是否有效
     * @param token
     * @return
     */
    public Boolean validateToken(String token) {
        if (ObjectUtil.isEmpty(token)) {
            return false;
        }
        String secret = getTokenSecret();
        boolean isValidSignature = JWTUtil.verify(token, secret.getBytes());
        return (!isTokenExpired(token) && isValidSignature);
    }

    /**
     * 校验refresh token是否有效
     * @param token
     * @return
     */
    public Boolean validateRefreshToken(String token) {
        if (ObjectUtil.isEmpty(token)) {
            return false;
        }
        String secret = getRefreshTokenSecret();
        boolean isValidSignature = JWTUtil.verify(token, secret.getBytes());
        return (!isTokenExpired(token) && isValidSignature);
    }
}
