package com.cool.core.util;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.util.ObjUtil;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryColumn;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
            resources = resolver.getResources("classpath*:com/cool/**/entity/**/*Entity.class");
            for (Resource r : resources) {
                String path = r.getURL().getPath();
                String className = path.substring(path.indexOf("com/cool"),
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
        ArrayList<String> excludeList = new ArrayList<>(List.of(excludeNames));
        return Arrays.stream(queryColumns).toList().stream()
            .filter(o -> !excludeList.contains(o.getName())).toList();
    }

    /**
     * 将bean的部分属性转换成map<br>
     * 可选拷贝哪些属性值，默认是不忽略值为{@code null}的值的。
     *
     * @param bean       bean
     * @param ignoreProperties 需要忽略拷贝的属性值，{@code null}或空表示拷贝所有值
     * @return Map
     * @since 5.8.0
     */
    public static Map<String, Object> toMap(Object bean, String... ignoreProperties) {
        int mapSize = 16;
        Editor<String> keyEditor = null;
        final Set<String> propertiesSet = CollUtil.set(false, ignoreProperties);
        propertiesSet.add("queryWrapper");
        mapSize = ignoreProperties.length;
        keyEditor = property -> !propertiesSet.contains(property) ? property : null;
        // 指明了要复制的属性 所以不忽略null值
        return BeanUtil.beanToMap(bean, new LinkedHashMap<>(mapSize, 1), false, keyEditor);
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