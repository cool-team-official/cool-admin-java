package com.cool.modules.base.entity.sys;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Index;

@Getter
@Setter
@Table(value = "base_sys_user_role", comment = "系统用户角色表")
public class BaseSysUserRoleEntity extends BaseEntity<BaseSysUserRoleEntity> {
    @Index
    @ColumnDefine(comment = "用户ID", type = "bigint")
    private Long userId;

    @Index
    @ColumnDefine(comment = "角色ID", type = "bigint")
    private Long roleId;
}
