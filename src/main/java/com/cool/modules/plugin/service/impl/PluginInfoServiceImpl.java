package com.cool.modules.plugin.service.impl;

import static com.cool.modules.plugin.entity.table.PluginInfoEntityTableDef.PLUGIN_INFO_ENTITY;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.mapper.PluginInfoMapper;
import com.cool.modules.plugin.service.PluginInfoService;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

/**
 * 插件信息服务类
 */
@Service
public class PluginInfoServiceImpl extends BaseServiceImpl<PluginInfoMapper, PluginInfoEntity>
        implements PluginInfoService {

    /**
     * 通过key获取插件信息
     */
    @Override
    public PluginInfoEntity getByKey(String key) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.and(PLUGIN_INFO_ENTITY.KEY.eq(key));
        return getOne(queryWrapper);
    }

    /**
     * 通过hook获取插件信息
     */
    @Override
    public PluginInfoEntity getPluginInfoEntityByHook(String hook) {
        QueryWrapper queryWrapper = getPluginInfoEntityQueryWrapper().and(PLUGIN_INFO_ENTITY.HOOK.eq(hook))
                .and(PLUGIN_INFO_ENTITY.STATUS.eq(1)).limit(1);
        return getOne(queryWrapper);
    }

    /**
     * 通过id获取插件信息
     */
    @Override
    public PluginInfoEntity getPluginInfoEntityById(Long id) {
        QueryWrapper queryWrapper = getPluginInfoEntityQueryWrapper().and(PLUGIN_INFO_ENTITY.ID.eq(id));
        return getOne(queryWrapper);
    }

    /**
     * 获取查询对象
     */
    private QueryWrapper getPluginInfoEntityQueryWrapper() {
        return QueryWrapper.create();
    }
}
