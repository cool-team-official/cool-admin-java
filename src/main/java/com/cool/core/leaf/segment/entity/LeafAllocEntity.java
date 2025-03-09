package com.cool.core.leaf.segment.entity;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import com.tangzc.mybatisflex.autotable.annotation.UniIndex;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "leaf_alloc", comment = "唯一id分配")
public class LeafAllocEntity  extends BaseEntity<LeafAllocEntity> {

    @UniIndex(name = "uk_key")
    @ColumnDefine(comment = "业务key ，比如orderId", length = 20, notNull = true)
    private String key;

    @ColumnDefine(comment = "当前最大id", defaultValue = "1", notNull = true)
    private Long maxId;

    @ColumnDefine(comment = "步长", defaultValue = "500", notNull = true)
    private Integer step;

    @ColumnDefine(comment = "描述")
    private String description;
}
