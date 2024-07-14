package com.cool.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cool.file.oss")
public class OssFileProperties {
    // accessKeyId
    private String accessKeyId;
    // accessKeySecret
    private String accessKeySecret;
    // 文件空间
    private String bucket;
    // 地址
    private String endpoint;
    // 超时时间
    private Long timeout;
}
