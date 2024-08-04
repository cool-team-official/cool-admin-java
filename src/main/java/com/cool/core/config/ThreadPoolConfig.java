package com.cool.core.config;

import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    private final LogProperties logProperties;

    @Bean(name = "logTaskExecutor")
    public Executor loggingTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        int corePoolSize = Runtime.getRuntime().availableProcessors() * logProperties.getCorePoolSizeMultiplier();
        int maxPoolSize = corePoolSize * logProperties.getMaxPoolSizeMultiplier();
        int queueCapacity = maxPoolSize * logProperties.getQueueCapacityMultiplier();

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("logTask-");

        // 自定义拒绝策略
        executor.setRejectedExecutionHandler(new LogDiscardPolicy());
        executor.initialize();
        return executor;
    }
}
