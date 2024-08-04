package com.cool.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cool.log")
public class LogProperties {

    /**
     * 请求参数最大字节,超过请求参数不记录
     */
    private int maxByteLength;
    /**
     * 核心线程数的倍数
     */
    private int corePoolSizeMultiplier;
    /**
     * 最大线程数的倍数
     */
    private int maxPoolSizeMultiplier;
    /**
     * 队列容量的倍数
     */
    private int queueCapacityMultiplier;
}
