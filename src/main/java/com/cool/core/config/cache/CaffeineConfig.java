package com.cool.core.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
 
@Slf4j
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "CAFFEINE")
public class CaffeineConfig {

    @Value("${spring.cache.file}")
    private String cacheFile;

    @Value("${cool.cacheName}")
    private String cacheName;

    @Bean
    public Caffeine<Object, Object> caffeine() {
        return Caffeine.newBuilder().maximumSize(10000);
    }

    @Bean
    public CaffeineCacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        loadCache(cacheManager);
        return cacheManager;
    }

    @PostConstruct
    public void init() {
        File cacheDir = new File(cacheFile).getParentFile();
        if (!cacheDir.exists()) {
            if (cacheDir.mkdirs()) {
                log.info("Created directory: " + cacheDir.getAbsolutePath());
            } else {
                log.error("Failed to create directory: " + cacheDir.getAbsolutePath());
            }
        }
    }

    private void loadCache(CaffeineCacheManager cacheManager) {
        if (cacheManager == null) {
            log.error("CacheManager is null");
            return;
        }

        if (cacheFile == null || cacheFile.isEmpty()) {
            log.error("Cache file path is null or empty");
            return;
        }

        File file = new File(cacheFile);
        if (!file.exists()) {
            log.warn("Cache file does not exist: " + cacheFile);
            return;
        }
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            Map<Object, Object> cacheMap = (Map<Object, Object>) inputStream.readObject();
            com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache = Caffeine.newBuilder()
                .build();
            caffeineCache.putAll(cacheMap);
            cacheManager.registerCustomCache(cacheName, caffeineCache);
        } catch (IOException | ClassNotFoundException e) {
            log.error("loadCacheErr", e);
        }
    }

    @Bean
    public CacheLoader cacheLoader(CaffeineCacheManager cacheManager) {
        return new CacheLoader(cacheManager, cacheFile);
    }

    class CacheLoader {

        private final CaffeineCacheManager cacheManager;
        private final String cacheFile;

        public CacheLoader(CaffeineCacheManager cacheManager, String cacheFile) {
            this.cacheManager = cacheManager;
            this.cacheFile = cacheFile;
        }

        @EventListener(ContextClosedEvent.class)
        @Scheduled(fixedRate = 10000)
        public void persistCache() {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null
                && cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
                Map<Object, Object> cacheMap = ((com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache
                    .getNativeCache()).asMap();
                try (ObjectOutputStream outputStream = new ObjectOutputStream(
                    new FileOutputStream(cacheFile))) {
                    outputStream.writeObject(new HashMap<>(cacheMap));
                } catch (IOException e) {
                    log.error("persistCacheErr", e);
                }
            }
        }
    }
}
