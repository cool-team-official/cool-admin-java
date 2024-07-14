package com.cool.modules.base.entity.sys;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.tangzc.autotable.annotation.Index;

import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import com.mybatisflex.annotation.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Table(value = "base_sys_menu", comment = "系统菜单表")
public class BaseSysMenuEntity extends BaseEntity<BaseSysMenuEntity> {
    @Index
    @ColumnDefine(comment = "父菜单ID", type = "bigint")
    private Long parentId;

    @ColumnDefine(comment = "菜单名称")
    private String name;

    @ColumnDefine(comment = "权限")
    private String perms;

    @ColumnDefine(comment = "类型 0：目录 1：菜单 2：按钮", type = "tinyint", defaultValue = "0")
    private Integer type;

    @ColumnDefine(comment = "图标")
    private String icon;

    @ColumnDefine(comment = "排序", defaultValue = "0")
    private Integer orderNum;

    @ColumnDefine(comment = "菜单地址")
    private String router;

    @ColumnDefine(comment = "视图地址")
    private String viewPath;

    @ColumnDefine(comment = "路由缓存", defaultValue = "true")
    private Boolean keepAlive;

    @ColumnDefine(comment = "是否显示", defaultValue = "true")
    private Boolean isShow;

    @Column(ignore = true)
    private String parentName;

    @Column(ignore = true)
    private List<BaseSysMenuEntity> childMenus;
}
