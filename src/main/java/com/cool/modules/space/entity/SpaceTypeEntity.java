package com.cool.modules.space.entity;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import lombok.Getter;
import lombok.Setter;

/**
 * 图片空间信息分类
 */
@Getter
@Setter
@Table(value = "space_type", comment = "图片空间信息分类")
public class SpaceTypeEntity extends BaseEntity<SpaceTypeEntity> {
    @ColumnDefine(comment = "类别名称", notNull = true)
    private String name;

    @ColumnDefine(comment = "父分类ID")
    private Integer parentId;
}
