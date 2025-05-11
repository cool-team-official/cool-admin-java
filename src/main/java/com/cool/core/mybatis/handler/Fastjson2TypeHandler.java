package com.cool.core.mybatis.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class Fastjson2TypeHandler extends BaseJsonTypeHandler<Object> {

    private final Class<?> propertyType;
    private Class<?> genericType;
    private Type type;

    private boolean supportAutoType = false;

    public Fastjson2TypeHandler(Class<?> propertyType) {
        this.propertyType = propertyType;
        this.supportAutoType = propertyType.isInterface() || Modifier.isAbstract(propertyType.getModifiers());
    }


    public Fastjson2TypeHandler(Class<?> propertyType, Class<?> genericType) {
        this.propertyType = propertyType;
        this.genericType = genericType;
        this.type = TypeReference.collectionType((Class<? extends Collection>) propertyType, genericType);

        Type actualTypeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
        if (actualTypeArgument instanceof Class) {
            this.supportAutoType = ((Class<?>) actualTypeArgument).isInterface()
                    || Modifier.isAbstract(((Class<?>) actualTypeArgument).getModifiers());
        }
    }

    @Override
    protected Object parseJson(String json) {
        if (genericType != null && Collection.class.isAssignableFrom(propertyType)) {
            if (supportAutoType) {
                return JSON.parseArray(json, Object.class, JSONReader.Feature.SupportAutoType);
            } else {
                return JSON.parseObject(json, type);
            }

        } else {
            if (supportAutoType) {
                return JSON.parseObject(json, Object.class, JSONReader.Feature.SupportAutoType);
            } else {
                return JSON.parseObject(json, propertyType);
            }
        }
    }

    @Override
    protected String toJson(Object object) {
        if (supportAutoType) {
            return JSON.toJSONString(object
                    , JSONWriter.Feature.WriteMapNullValue
                    , JSONWriter.Feature.WriteNullListAsEmpty
                    , JSONWriter.Feature.WriteNullStringAsEmpty, JSONWriter.Feature.WriteClassName
            );
        } else {
            return JSON.toJSONString(object
                    , JSONWriter.Feature.WriteMapNullValue
                    , JSONWriter.Feature.WriteNullListAsEmpty
                    , JSONWriter.Feature.WriteNullStringAsEmpty
            );
        }
    }
}
