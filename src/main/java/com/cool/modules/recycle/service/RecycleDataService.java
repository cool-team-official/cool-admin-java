package com.cool.modules.recycle.service;

import cn.hutool.json.JSONObject;
import com.cool.core.base.BaseService;
import com.cool.modules.recycle.entity.RecycleDataEntity;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

public interface RecycleDataService extends BaseService<RecycleDataEntity> {
    Object page(JSONObject requestParams, Page<RecycleDataEntity> page, QueryWrapper queryWrapper);

    /**
     * 恢复数据
     * 
     * @param ids
     * @return
     */
    Boolean restore(List<Long> ids);
}
