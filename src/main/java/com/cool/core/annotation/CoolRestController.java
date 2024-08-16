package com.cool.core.annotation;

import java.lang.annotation.*;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 自定义路由注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@RequestMapping
public @interface CoolRestController {

    @AliasFor(annotation = RequestMapping.class)
    String name() default "";

    @AliasFor(annotation = RequestMapping.class)
    String[] value() default {};

    String[] api() default {};

    /**
     * 如前缀: /admin/goods/searchKeyword
     * 没指定该字段 cname="searchKeyword",
     * 按规则是解析为: /admin/goods/search/keyword
     * 前端和node版本已经定义为 searchKeyword,没按规则解析，使用该字段自定义规则 进行兼容
     * com.cool.core.request.prefix.AutoPrefixUrlMapping#getCName(java.lang.Class, java.lang.String)
     */
    String cname() default "";
}
