package com.cool.core.code;

import lombok.Data;

/**
 * 代码模型
 */
@Data
public class CodeModel {
    /**
     * 类型 后台还是对外的接口 admin app
     */
    private CodeTypeEnum type;
    /**
     * 名称
     */
    private String name;
    /**
     * 模块
     */
    private String module;

    /**
     * 子模块
     */
    private String subModule;

    /**
     * 实体类
     */
    private String entity;

    public void setEntity(Class entity) {
        this.entity = entity.getSimpleName().replace("Entity", "");
    }
}
