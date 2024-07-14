package com.cool.core.request.prefix;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.cool.core.annotation.CoolRestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * 自动配置模块的路由
 */
@Slf4j
public class AutoPrefixUrlMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        CoolRestController[] annotations = handlerType.getAnnotationsByType(CoolRestController.class);
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        String packageName = handlerType.getPackage().getName();
        if (info != null && annotations.length > 0 && annotations[0].value().length == 0
                && packageName.contains("modules")) {
            if (!checkApis(annotations, info)) {
                return null;
            }
            String prefix = getPrefix(handlerType, packageName);
            String cName = getCName(handlerType, prefix);
            info = info.mutate().paths(prefix + "/" + cName).build().combine(info);
        }
        return info;
    }

    /**
     * 根据配置检查是否构建路由
     *
     * @param annotations 注解
     * @param info        路由信息
     * @return 是否需要构建路由
     */
    private boolean checkApis(CoolRestController[] annotations, RequestMappingInfo info) {
        String[] apis = new String[] { "add", "delete", "update", "page", "list", "info" };
        if (info.getPathPatternsCondition() == null) {
            return true;
        }
        List<String> setApis;
        if (ArrayUtil.isNotEmpty(annotations)) {
            CoolRestController coolRestController = annotations[0];
            setApis = CollUtil.toList(coolRestController.api());

            Set<String> methodPaths = info.getPathPatternsCondition().getPatternValues();
            String methodPath = methodPaths.iterator().next().replace("/", "");
            if (!CollUtil.toList(apis).contains(methodPath)) {
                return true;
            } else {
                return setApis.contains(methodPath);
            }
        }
        return false;
    }

    /**
     * 根据Controller名称构建路由地址
     *
     * @param handlerType 类
     * @param prefix      路由前缀
     * @return url地址
     */
    private String getCName(Class<?> handlerType, String prefix) {
        String name = handlerType.getName();
        String[] names = name.split("[.]");
        name = names[names.length - 1];
        return name.toLowerCase().replace("controller", "").replace(prefix.replace("/", ""), "");
    }

    /**
     * 构建路由前缀
     *
     * @param handlerType 类
     * @param packageName 包名
     * @return 返回路由前缀
     */
    private String getPrefix(Class<?> handlerType, String packageName) {
        String dotPath = packageName.split("modules")[1]; // 将包路径中多于的部分截取掉
        String[] dotPaths = dotPath.replace(".controller", "").split("[.]");
        List<String> paths = CollUtil.toList(dotPaths);
        paths.removeIf(String::isEmpty);
        // 第一和第二位互换位置
        String p0 = paths.get(0);
        String p1 = paths.get(1);
        paths.set(0, p1);
        paths.set(1, p0);
        dotPath = "/" + CollUtil.join(paths, "/");
        return dotPath;
    }

}