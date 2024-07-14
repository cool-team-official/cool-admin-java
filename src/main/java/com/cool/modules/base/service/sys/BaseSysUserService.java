package com.cool.modules.base.service.sys;

import cn.hutool.core.lang.Dict;
import com.cool.core.base.BaseService;
import com.cool.modules.base.entity.sys.BaseSysUserEntity;

/**
 * 系统用户
 */
public interface BaseSysUserService extends BaseService<BaseSysUserEntity> {
    /**
     * 修改用户信息
     *
     * @param body 用户信息
     */
    void personUpdate(Long userId, Dict body);

    /**
     * 移动部门
     *
     * @param departmentId 部门ID
     * @param userIds      用户ID集合
     */
    void move(Long departmentId, Long[] userIds);
}
