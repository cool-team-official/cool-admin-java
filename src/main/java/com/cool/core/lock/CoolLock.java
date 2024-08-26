package com.cool.core.lock;


import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoolLock {
    // 缓存类型
    @Value("${spring.cache.type}")
    private String type;

    @Value("${cool.cacheName}")
    private String cacheName;

    private final CacheManager cacheManager;
    private RedisCacheWriter redisCache ;

    // 非redis方式时使用
    private static final Map<String, Lock> lockMap = new ConcurrentHashMap<>();

    private static final String LOCK_PREFIX = "lock:";

    @PostConstruct
    private void init() {
        this.type = type.toLowerCase();
        if (type.equalsIgnoreCase(CacheType.REDIS.name())) {
            redisCache = (RedisCacheWriter) Objects.requireNonNull(cacheManager.getCache(cacheName))
                .getNativeCache();
        }
    }
    /**
     * 尝试获取锁
     *
     * @param key 锁的 key
     * @param expireTime 锁的过期时间
     * @return 如果成功获取锁则返回 true，否则返回 false
     */
    public boolean tryLock(String key, Duration expireTime) {
        String lockKey = getLockKey(key);
        if (type.equalsIgnoreCase(CacheType.CAFFEINE.name())) {
            Lock lock = lockMap.computeIfAbsent(lockKey, k -> new ReentrantLock());
            return lock.tryLock();
        }
        byte[] lockKeyBytes = lockKey.getBytes();
        // 使用 putIfAbsent 来尝试设置锁，如果成功返回 true，否则返回 false
        return redisCache.putIfAbsent(cacheName, lockKeyBytes, new byte[0], expireTime) == null;
    }

    /**
     * 释放锁
     */
    public void unlock(String key) {
        String lockKey = getLockKey(key);
        if (type.equalsIgnoreCase(CacheType.CAFFEINE.name())) {
            Lock lock = lockMap.get(lockKey);
            if (lock != null && lock.tryLock()) {
                lock.unlock();
                lockMap.remove(lockKey);
            }
            return;
        }
        redisCache.remove(cacheName, lockKey.getBytes());
    }

    /**
     * 拼接锁前缀
     */
    private String getLockKey(String key) {
        return LOCK_PREFIX + key;
    }

    /**
     * 等待锁
     *
     * @param key 锁的 key
     * @param expireTime 锁的过期时间
     * @return 如果成功获取锁则返回 true，否则返回 false
     */
    public boolean waitForLock(String key, Duration expireTime, Duration waitTime) {
        long endTime = System.currentTimeMillis() + waitTime.toMillis();
        while (System.currentTimeMillis() < endTime) {
            if (tryLock(key, expireTime)) {
                return true;
            }
            // 等待锁释放
            try {
                Thread.sleep(100); // 可以根据需要调整等待时间
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
}
