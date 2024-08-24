package com.cool.core.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    @Bean(name = "cachedThreadPool")
    public ExecutorService cachedThreadPool() {
        // 创建一个虚拟线程池，每个任务使用一个虚拟线程执行
        return Executors.newCachedThreadPool();
    }
}
