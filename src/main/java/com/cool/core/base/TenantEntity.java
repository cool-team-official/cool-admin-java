package com.cool.core.base;

import com.mybatisflex.core.activerecord.Model;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Index;

/** 租户ID实体类 */
@Getter
@Setter
public class TenantEntity<T extends Model<T>> extends BaseEntity<T> {
    @Index
    @ColumnDefine(comment = "租户id")
    protected Long tenantId;
}