package com.cool.modules.base.entity.sys;

import com.cool.core.base.BaseEntity;

import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import com.mybatisflex.annotation.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "base_sys_role_menu", comment = "系统角色菜单表")
public class BaseSysRoleMenuEntity extends BaseEntity<BaseSysRoleMenuEntity> {
    @ColumnDefine(comment = "菜单", type = "bigint")
    private Long menuId;

    @ColumnDefine(comment = "角色ID", type = "bigint")
    private Long roleId;
}
