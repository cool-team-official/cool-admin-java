package com.cool.core.annotation;

import com.cool.core.enums.AdminComponentsEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EpsField {
    String component() default AdminComponentsEnum.INPUT;
}
