package com.cool.core.mybatis.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.exception.FlexExceptions;

import java.io.IOException;
import java.util.Collection;

public class JacksonTypeHandler extends BaseJsonTypeHandler<Object> {

    private static ObjectMapper objectMapper;
    private final Class<?> propertyType;
    private Class<?> genericType;
    private JavaType javaType;

    public JacksonTypeHandler(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    public JacksonTypeHandler(Class<?> propertyType, Class<?> genericType) {
        this.propertyType = propertyType;
        this.genericType = genericType;
    }

    @Override
    protected Object parseJson(String json) {
        try {
            if (genericType != null && Collection.class.isAssignableFrom(propertyType)) {
                return getObjectMapper().readValue(json, getJavaType());
            } else {
                return getObjectMapper().readValue(json, propertyType);
            }
        } catch (IOException e) {
            throw FlexExceptions.wrap(e, "Can not parseJson by JacksonTypeHandler: " + json);
        }
    }

    @Override
    protected String toJson(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw FlexExceptions.wrap(e, "Can not convert object to Json by JacksonTypeHandler: " + object);
        }
    }


    public JavaType getJavaType() {
        if (javaType == null){
            javaType = getObjectMapper().getTypeFactory().constructCollectionType((Class<? extends Collection>) propertyType, genericType);
        }
        return javaType;
    }

    public static ObjectMapper getObjectMapper() {
        if (null == objectMapper) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        JacksonTypeHandler.objectMapper = objectMapper;
    }

}