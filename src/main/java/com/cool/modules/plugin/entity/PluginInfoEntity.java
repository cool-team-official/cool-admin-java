package com.cool.modules.plugin.entity;

import com.cool.core.base.BaseEntity;
import com.cool.core.config.PluginJson;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.cool.core.mybatis.handler.Fastjson2TypeHandler;
import com.cool.core.mybatis.handler.JacksonTypeHandler;
import com.cool.core.annotation.ColumnDefine;
import com.tangzc.mybatisflex.autotable.annotation.UniIndex;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Ignore;
import org.dromara.autotable.annotation.Index;

@Getter
@Setter
@Table(value = "plugin_info", comment = "插件信息")
public class PluginInfoEntity extends BaseEntity<PluginInfoEntity> {

    @ColumnDefine(comment = "名称")
    private String name;

    @ColumnDefine(comment = "简介")
    private String description;

    @UniIndex
    @ColumnDefine(comment = "实例对象")
    private String key;

    @Index
    @ColumnDefine(comment = "Hook", length = 50)
    private String hook;

    @ColumnDefine(comment = "描述", type = "text")
    private String readme;

    @ColumnDefine(comment = "版本")
    private String version;

    @ColumnDefine(comment = "Logo(base64)", type = "text", notNull = true)
    private String logo;

    @ColumnDefine(comment = "作者")
    private String author;

    @ColumnDefine(comment = "状态 0-禁用 1-启用", defaultValue = "1")
    private Integer status;

    @ColumnDefine(comment = "插件的plugin.json", type = "json", notNull = true)
    @Column(typeHandler = Fastjson2TypeHandler.class)
    private PluginJson pluginJson;

    @ColumnDefine(comment = "配置", type = "json")
    @Column(typeHandler = JacksonTypeHandler.class)
    private Object config;

    @Ignore
    @Column(ignore = true)
    public String keyName;

    public String getKeyName() {
        return key;
    }
}
