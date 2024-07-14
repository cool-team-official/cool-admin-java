package com.cool.modules.base.service.sys;

import com.cool.core.base.BaseService;
import com.cool.modules.base.entity.sys.BaseSysConfEntity;

/**
 * 系统配置
 */
public interface BaseSysConfService extends BaseService<BaseSysConfEntity> {
    /**
     * 更新配置
     *
     * @param key   键
     * @param value 值
     */
    void updateValue(String key, String value);

    /**
     * 获得值
     *
     * @param key 键
     * @return 值
     */
    String getValue(String key);

    /**
     * 获得值（带缓存）
     *
     * @param key 键
     * @return 值
     */
    String getValueWithCache(String key);

    /**
     * 设置值
     *
     * @param key 键
     * @param value 值
     */
    void setValue(String key, String value);
}
