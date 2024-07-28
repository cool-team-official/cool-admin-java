package com.cool.modules.plugin.service;

import com.cool.core.base.BaseService;
import com.cool.modules.plugin.entity.PluginInfoEntity;

public interface PluginInfoService extends BaseService<PluginInfoEntity> {
    PluginInfoEntity getByKey(String key);

    PluginInfoEntity getPluginInfoEntityByHook(String hook);

    PluginInfoEntity getPluginInfoEntityById(Long id);
}
