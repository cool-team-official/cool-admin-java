package com.cool.core.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.activerecord.Model;
import com.mybatisflex.core.query.QueryWrapper;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Ignore;

/**
 * 基础实体类
 */
@Getter
@Setter
public abstract class BaseEntity<T extends Model<T>> extends Model<T> implements Serializable {

    @Id(keyType = KeyType.Auto, comment = "ID")
    protected Long id;

    @Column(onInsertValue = "now()")
    @ColumnDefine(comment = "创建时间")
    protected Date createTime;

    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    @ColumnDefine(comment = "更新时间")
    protected Date updateTime;

    @Ignore
    @Column(ignore = true)
    @JsonIgnore
    private QueryWrapper queryWrapper;
}