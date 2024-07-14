package com.cool.modules.plugin.service;

import com.cool.core.base.BaseService;
import com.cool.modules.plugin.entity.PluginInfoEntity;

public interface PluginInfoService extends BaseService<PluginInfoEntity> {
    PluginInfoEntity getByKeyNoJarFile(String key);

    PluginInfoEntity getPluginInfoEntityByHookNoJarFile(String hook);

    PluginInfoEntity getPluginInfoEntityByIdNoJarFile(Long id);
}
