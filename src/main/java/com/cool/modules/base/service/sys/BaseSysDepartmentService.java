package com.cool.modules.base.service.sys;

import com.cool.core.base.BaseService;
import com.cool.modules.base.entity.sys.BaseSysDepartmentEntity;

import java.util.List;

/**
 * 系统部门
 */
public interface BaseSysDepartmentService extends BaseService<BaseSysDepartmentEntity> {
    /**
     * 排序
     *
     * @param list 新的排序
     */
    void order(List<BaseSysDepartmentEntity> list);
}
