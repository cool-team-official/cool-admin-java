package com.cool.core.util;

import com.cool.core.annotation.CoolPlugin;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Slf4j
public class AnnotationUtils {

    /**
     * 判断一个类是否有 Spring 核心注解
     *
     * @param clazz 要检查的类
     * @return true 如果该类上添加了相应的 Spring 注解；否则返回 false
     */
    public static boolean hasSpringAnnotation(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        // 是否是接口
        if (clazz.isInterface()) {
            return false;
        }
        // 是否是抽象类
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }

        try {
            if (clazz.getAnnotation(Component.class) != null || clazz.getAnnotation(Repository.class) != null
                    || clazz.getAnnotation(Service.class) != null || clazz.getAnnotation(Controller.class) != null
                    || clazz.getAnnotation(Configuration.class) != null) {
                return true;
            }
        } catch (Exception e) {
            log.error("出现异常：{}", e.getMessage());
        }
        return false;
    }

    /**
     * 插件
     */
    public static boolean hasCoolPluginAnnotation(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        // 是否是接口
        if (clazz.isInterface()) {
            return false;
        }
        // 是否是抽象类
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (clazz.getAnnotation(
                    (Class<? extends Annotation>) contextClassLoader.loadClass(CoolPlugin.class.getName())) != null) {
                return true;
            }
        } catch (Exception e) {
            log.error("出现异常：{}", e.getMessage(), e);
        }
        return false;
    }
}