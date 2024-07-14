package com.cool.modules.base.entity.sys;

import com.cool.core.base.BaseEntity;

import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import com.mybatisflex.annotation.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "base_sys_role_department", comment = "系统角色部门")
public class BaseSysRoleDepartmentEntity extends BaseEntity<BaseSysRoleDepartmentEntity> {

    @ColumnDefine(comment = "角色ID", type = "bigint")
    private Long roleId;

    @ColumnDefine(comment = "部门ID", type = "bigint")
    private Long departmentId;
}
