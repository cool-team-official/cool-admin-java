package com.cool.core.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Map;

@Data
public class PluginJson {
    /**
     * 插件名称
     */
    private String name;
    /**
     * 插件标识
     */
    private String key;
    /**
     * 插件钩子，比如替换系统的上传组件，upload
     */
    private String hook;
    /**
     * 版本号
     */
    private String version;
    /**
     * 插件描述
     */
    private String description;
    /**
     * 作者
     */
    private String author;
    /**
     * 插件 logo，建议尺寸 256x256
     */
    private String logo;
    /**
     * 插件介绍，会展示在插件的详情中
     */
    private String readme;
    /**
     * 插件配置， 每个插件的配置各不相同
     */
    private Map<String, Object> config;

    /**
     * jar包存放路径
     */
    private String jarPath;

    /**
     * 同名hook id
     */
    @JsonIgnore
    private Long sameHookId;
}
