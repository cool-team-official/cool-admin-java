package com.cool.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * token配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cool.token")
public class TokenProperties {
    // token 过期时间
    private Long expire;
    // refreshToken 过期时间
    private Long refreshExpire;
}
