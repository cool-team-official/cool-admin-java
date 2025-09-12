package com.cool.core.annotation;

import org.dromara.autotable.annotation.ColumnComment;
import org.dromara.autotable.annotation.ColumnDefault;
import org.dromara.autotable.annotation.ColumnNotNull;
import org.dromara.autotable.annotation.ColumnType;
import org.dromara.autotable.annotation.enums.DefaultValueEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ColumnType
@ColumnNotNull
@ColumnDefault
@ColumnComment("")
public @interface ColumnDefine {

    /**
     * 字段类型：不填默认使用属性的数据类型进行转换，转换失败的字段不会添加
     *
     * @return 字段类型
     */
    @AliasFor(annotation = ColumnType.class, attribute = "value")
    String type() default "";

    /**
     * 字段长度,默认是-1，小于0相当于null
     *
     * @return 默认字段长度
     */
    @AliasFor(annotation = ColumnType.class, attribute = "length")
    int length() default -1;

    /**
     * 小数点长度，默认是-1，小于0相当于null
     *
     * @return 小数点长度
     */
    @AliasFor(annotation = ColumnType.class, attribute = "decimalLength")
    int decimalLength() default -1;

    /**
     * 是否为可以为null，true是可以，false是不可以，默认为true
     *
     * @return 是否为可以为null，true是不可以，false是可以，默认为false
     */
    @AliasFor(annotation = ColumnNotNull.class, attribute = "value")
    boolean notNull() default false;

    /**
     * 默认值，默认为null
     *
     * @return 默认值
     */
    @AliasFor(annotation = ColumnDefault.class, attribute = "type")
    DefaultValueEnum defaultValueType() default DefaultValueEnum.UNDEFINED;

    /**
     * 默认值，默认为null
     *
     * @return 默认值
     */
    @AliasFor(annotation = ColumnDefault.class, attribute = "value")
    String defaultValue() default "";

    /**
     * 数据表字段备注
     *
     * @return 默认值，默认为空
     */
    @AliasFor(annotation = ColumnComment.class, attribute = "value")
    String comment() default "";
}