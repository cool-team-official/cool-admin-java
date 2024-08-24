package com.cool.core.enums;

/**
 * 查询模式决定返回值
 */
public enum QueryModeEnum {
    ENTITY, // 实体（默认）
    ENTITY_WITH_RELATIONS, // 实体关联查询(如实体字段上加 @RelationOneToMany 等注解)
    CUSTOM , // 自定义，默认为Map
}
