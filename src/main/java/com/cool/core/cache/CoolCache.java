package com.cool.core.cache;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.cool.core.util.ConvertUtil;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.stereotype.Component;

/**
 * 缓存工具类
 */
@EnableCaching
@Configuration
@Component
@RequiredArgsConstructor
public class CoolCache {

    // 缓存类型
    @Value("${spring.cache.type}")
    private String type;

    // redis
    public RedisCacheWriter redisCache;

    private Cache cache;

    @Value("${cool.cacheName}")
    private String cacheName;

    private final static String NULL_VALUE = "@_NULL_VALUE$@";

    final private CacheManager cacheManager;

    @PostConstruct
    private void init() {
        cache = cacheManager.getCache(cacheName);
        this.type = type.toLowerCase();
        assert cache != null : "Cache not found: " + cacheName; // Ensure cache is not null
        if (type.equalsIgnoreCase(CacheType.REDIS.name())) {
            redisCache = (RedisCacheWriter) cache.getNativeCache();
        }
    }

    /**
     * 数据来源
     */
    public interface ToCacheData {
        Object apply();
    }

    /**
     * 删除缓存
     *
     * @param keys 一个或多个key
     */
    public void del(String... keys) {
        if (type.equalsIgnoreCase(CacheType.CAFFEINE.name())) {
            Arrays.stream(keys).forEach(o -> cache.evict(o));
        }
        if (type.equalsIgnoreCase(CacheType.REDIS.name())) {
            Arrays.stream(keys).forEach(key -> redisCache.remove(cacheName, key.getBytes()));
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     */
    public Object get(String key) {
        Object ifNullValue = getIfNullValue(key);
        if (ObjUtil.equals(ifNullValue, NULL_VALUE)) {
            return null;
        }
        return ifNullValue;
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     */
    public Object get(String key, Duration duration, ToCacheData toCacheData) {
        Object ifNullValue = getIfNullValue(key);
        if (ObjUtil.equals(ifNullValue, NULL_VALUE)) {
            return null;
        }
        if (ObjUtil.isEmpty(ifNullValue)) {
            Object obj = toCacheData.apply();
            set(key, obj, duration.toSeconds());
            return obj;
        }
        return ifNullValue;
    }

    private Object getIfNullValue(String key) {
        if (type.equalsIgnoreCase(CacheType.CAFFEINE.name())) {
            Cache.ValueWrapper valueWrapper = cache.get(key);
            if (valueWrapper != null) {
                return valueWrapper.get(); // 获取实际的缓存值
            }
        }
        if (type.equalsIgnoreCase(CacheType.REDIS.name())) {
            byte[] bytes = redisCache.get(cacheName, key.getBytes());
            if (bytes != null && bytes.length > 0) {
                return ConvertUtil.toObject(bytes);
            }
        }
        return null;
    }

    /**
     * 获得对象
     *
     * @param key       键
     * @param valueType 值类型
     */
    public <T> T get(String key, Class<T> valueType) {
        Object result = get(key);
        if (result != null && JSONUtil.isTypeJSONObject(result.toString())) {
            return JSONUtil.parseObj(result).toBean(valueType);
        }
        return result != null ? (T) result : null;
    }

    /**
     * 获得缓存类型
     */
    public String getMode() {
        return this.type;
    }

    /**
     * 获得原生缓存实例
     */
    public Object getMetaCache() {
        return this.cache;
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        set(key, value, 0);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param ttl   时间(秒) time要大于0 如果time小于等于0 将设置无限期
     */
    public void set(String key, Object value, long ttl) {
        if (ObjUtil.isNull(value)) {
            value = NULL_VALUE;
        }
        if (type.equalsIgnoreCase(CacheType.CAFFEINE.name())) {
            // 放入缓存
            cache.put(key, value);
        } else if (type.equalsIgnoreCase(CacheType.REDIS.name())) {
            redisCache.put(cacheName, key.getBytes(), ObjectUtil.serialize(value),
                java.time.Duration.ofSeconds(ttl));
        }
    }
}
