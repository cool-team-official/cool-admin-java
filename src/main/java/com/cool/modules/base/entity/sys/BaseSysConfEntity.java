package com.cool.modules.base.entity.sys;

import com.cool.core.base.BaseEntity;
import com.tangzc.autotable.annotation.enums.IndexTypeEnum;

import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import com.mybatisflex.annotation.Table;
import com.tangzc.autotable.annotation.Index;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统配置
 */
@Getter
@Setter
@Table(value = "base_sys_conf", comment = "系统配置表")
public class BaseSysConfEntity extends BaseEntity<BaseSysConfEntity> {
    @Index(type = IndexTypeEnum.UNIQUE)
    @ColumnDefine(comment = "配置键", notNull = true)
    private String cKey;

    @ColumnDefine(comment = "值", notNull = true, type = "text")
    private String cValue;
}
