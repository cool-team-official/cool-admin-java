package com.cool.modules.base.entity.sys;

import com.cool.core.base.BaseEntity;

import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import com.mybatisflex.annotation.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "base_sys_user_role", comment = "系统用户角色表")
public class BaseSysUserRoleEntity extends BaseEntity<BaseSysUserRoleEntity> {
    @ColumnDefine(comment = "用户ID", type = "bigint")
    private Long userId;

    @ColumnDefine(comment = "角色ID", type = "bigint")
    private Long roleId;
}
