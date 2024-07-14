package com.cool.core.base.service;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.TypeUtil;
import com.cool.core.util.SpringContextUtils;
import com.mybatisflex.core.BaseMapper;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class MapperProviderService {

    private Map<Class<?>, BaseMapper<?>> mapperMap;

    /**
     * 初始化mapperMap，key 为entityClass，value 为 mapper
     */
    private void init() {
        // 获取所有BaseMapper类型的Bean
        Map<String, BaseMapper> beansOfType = SpringContextUtils.getBeansOfType(BaseMapper.class);
        mapperMap = new HashMap<>();
        for (BaseMapper mapper : beansOfType.values()) {
            // 通过反射获取泛型参数，即实体类
            Class<?> entityClass = getGenericType(mapper);
            if (entityClass != null) {
                mapperMap.put(entityClass, mapper);
            }
        }
    }

    /**
     * 通过entity类获取 对应的mapper接口
     */
    public <T> BaseMapper<T> getMapperByEntityClass(Class<T> entityClass) {
        if (ObjUtil.isEmpty(mapperMap)) {
            init();
        }
        return (BaseMapper<T>) mapperMap.get(entityClass);
    }

    /**
     * 获取mapper对应的entity对象
     */
    private Class<?> getGenericType(BaseMapper<?> mapper) {
        // 使用  获取泛型参数类型
        Type[] types = mapper.getClass().getGenericInterfaces();
        Type typeArgument = TypeUtil.getTypeArgument(types[0], 0);
        return ObjUtil.isEmpty(typeArgument) ? null : (Class<?>) typeArgument;
    }
}