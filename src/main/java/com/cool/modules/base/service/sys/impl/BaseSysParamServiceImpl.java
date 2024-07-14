package com.cool.modules.base.service.sys.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.cool.core.base.BaseServiceImpl;
import com.cool.core.cache.CoolCache;
import com.cool.modules.base.entity.sys.BaseSysParamEntity;
import com.cool.modules.base.mapper.sys.BaseSysParamMapper;
import com.cool.modules.base.service.sys.BaseSysParamService;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统参数配置
 */
@Service
@RequiredArgsConstructor
public class BaseSysParamServiceImpl extends BaseServiceImpl<BaseSysParamMapper, BaseSysParamEntity>
    implements BaseSysParamService {

    final private CoolCache coolCache;

    @Override
    public String htmlByKey(String key) {
        String data = dataByKey(key);
        return "<html><body>" + (StrUtil.isNotEmpty(data) ? data : "key notfound")
            + "</body></html>";
    }

    @Override
    public String dataByKey(String key) {
        BaseSysParamEntity baseSysParamEntity = coolCache.get(key, BaseSysParamEntity.class);
        if (baseSysParamEntity == null) {
            baseSysParamEntity = getOne(
                QueryWrapper.create().eq(BaseSysParamEntity::getKeyName, key));
        }
        if (baseSysParamEntity != null) {
            coolCache.set("param:" + baseSysParamEntity.getKeyName(), baseSysParamEntity);
            return baseSysParamEntity.getData();
        }
        return null;
    }

    @Override
    public void modifyAfter(JSONObject requestParams, BaseSysParamEntity baseSysParamEntity) {
        List<BaseSysParamEntity> list = this.list();
        list.forEach(e -> {
            coolCache.set("param:" + e.getKeyName(), e);
        });
    }
}