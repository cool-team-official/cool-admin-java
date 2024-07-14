package com.cool.core.util;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjUtil;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryColumn;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class EntityUtils {

    private static Map<String, Class<?>> TABLE_MAP;

    public static Set<String> findEntityClassName() {
        Set<String> entitySet = new HashSet<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = null;
        try {
            resources = resolver.getResources("classpath*:com/cool/modules/**/entity/**/*.class");
            for (Resource r : resources) {
                String path = r.getURL().getPath();
                String className = path.substring(path.indexOf("com/cool/modules"),
                    path.lastIndexOf('.')).replace('/', '.');
                entitySet.add(className);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return entitySet;
    }

    public static Map<String, Class<?>> findTableMap() {
        if (ObjUtil.isEmpty(TABLE_MAP)) {
            init();
        }
        return TABLE_MAP;
    }

    private static void init() {
        Set<String> classNames = EntityUtils.findEntityClassName();
        TABLE_MAP = new HashMap<>();
        classNames.forEach(className -> {
            Class<?> entityClass;
            try {
                entityClass = Class.forName(className);
                Table tableAnnotation = AnnotationUtil.getAnnotation(entityClass, Table.class);
                // key表名，value 实体对象
                TABLE_MAP.put(tableAnnotation.value(), entityClass);
            } catch (Exception e) {
                // do nothing
            }
        });
    }

    /**
     * 获取实体类及其父类的字段名数组（排除指定字段）
     *
     * @return 字段名数组
     */
    public static QueryColumn[] getFieldNamesWithSuperClass(QueryColumn[] queryColumns,
        String... excludeNames) {
        return getFieldNamesListWithSuperClass(queryColumns, excludeNames).toArray(
            new QueryColumn[0]);
    }

    public static List<QueryColumn> getFieldNamesListWithSuperClass(QueryColumn[] queryColumns,
        String... excludeNames) {
        return Arrays.stream(queryColumns).toList().stream()
            .filter(o -> ObjUtil.equals(o.getName(), excludeNames)).toList();
    }

    /**
     * 检查字段名是否在排除列表中
     *
     * @param fieldName    要检查的字段名
     * @param excludeNames 排除的字段名数组
     * @return 是否在排除列表中
     */
    private static boolean isExcluded(String fieldName, String[] excludeNames) {
        for (String excludeName : excludeNames) {
            if (fieldName.equals(excludeName)) {
                return true;
            }
        }
        return false;
    }

}