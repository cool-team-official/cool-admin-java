package com.cool.core.security;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 忽略地址配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ignored")
public class IgnoredUrlsProperties {

    // 忽略后台校验权限列表
    private List<String> adminAuthUrls = new ArrayList<>();

    // 忽略记录请求日志列表
    private List<String> logUrls = new ArrayList<>();
}
