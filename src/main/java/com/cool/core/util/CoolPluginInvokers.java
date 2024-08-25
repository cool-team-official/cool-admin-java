package com.cool.core.util;

import cn.hutool.json.JSONUtil;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.plugin.consts.PluginConsts;
import com.cool.core.plugin.service.DynamicJarLoaderService;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * 插件调用封装
 */
@Slf4j
public class CoolPluginInvokers {

    private static final DynamicJarLoaderService dynamicJarLoaderService = SpringContextUtils
        .getBean(DynamicJarLoaderService.class);

    /**
     * 插件默认调用入口
     */
    public static Object invokePlugin(String key, String... params) {
        return invoke(key, PluginConsts.invokePluginMethodName, params);
    }

    /**
     * 设置插件配置信息
     */
    public static void setPluginJson(String key, PluginInfoEntity entity) {
        invoke(key, PluginConsts.setPluginJson, JSONUtil.toJsonStr(entity.getPluginJson()));
        setApplicationContext(key);
    }

    /**
     * 设置 ApplicationContext 到插件类中
     */
    public static void setApplicationContext(String key) {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread()
                .setContextClassLoader(dynamicJarLoaderService.getDynamicJarClassLoader(key));
            Object beanInstance = dynamicJarLoaderService.getBeanInstance(key);
            Method method = beanInstance.getClass().getSuperclass()
                .getMethod(PluginConsts.setApplicationContext,
                    ApplicationContext.class);
            method.invoke(beanInstance, SpringContextUtils.applicationContext);
        } catch (Exception e) {
            log.error("setApplicationContext err", e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    /**
     * 反射调用插件
     *
     * @param key        插件key
     * @param methodName 插件方法
     * @param params     参数
     */
    public static Object invoke(String key, String methodName, Object... params) {
        Object beanInstance = dynamicJarLoaderService.getBeanInstance(key);
        CoolPreconditions.checkEmpty(beanInstance, "未找到该插件：{}, 请前往插件市场进行安装",key);
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            // 设置当前线程的上下文类加载器为插件的类加载器
            Thread.currentThread()
                .setContextClassLoader(dynamicJarLoaderService.getDynamicJarClassLoader(key));
            log.info("调用插件类: {}, 方法: {} 参数: {}", key, methodName, params);
            return invoke(beanInstance, methodName, params);
        } catch (Exception e) {
            log.error("调用插件{}.{}失败", key, methodName, e);
            CoolPreconditions.alwaysThrow("调用插件{}.{}失败 {}", key, methodName, e.getMessage());
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
        return null;
    }

    /**
     * 反射调用插件
     *
     * @param beanInstance 插件实例对象
     * @param methodName   插件方法
     * @param params       参数
     */
    private static Object invoke(Object beanInstance, String methodName, Object[] params)
        throws InvocationTargetException, IllegalAccessException {
        Class<?>[] paramTypes = Arrays.stream(params).map(Object::getClass)
            .toArray(Class<?>[]::new);
        Method method = findMethod(beanInstance.getClass(), methodName, paramTypes);
        CoolPreconditions.check(method == null, "No such method: {} with parameters {}", methodName,
            Arrays.toString(paramTypes));
        if (method.isVarArgs()) {
            // 处理可变参数调用
            int varArgIndex = method.getParameterTypes().length - 1;
            Object[] varArgs = (Object[]) java.lang.reflect.Array.newInstance(
                method.getParameterTypes()[varArgIndex].getComponentType(),
                params.length - varArgIndex);
            System.arraycopy(params, varArgIndex, varArgs, 0, varArgs.length);
            Object[] methodArgs = new Object[varArgIndex + 1];
            System.arraycopy(params, 0, methodArgs, 0, varArgIndex);
            methodArgs[varArgIndex] = varArgs;
            return method.invoke(beanInstance, methodArgs);
        } else {
            // 正常调用
            return method.invoke(beanInstance, params);
        }
    }

    // 查找方法，包括处理可变参数
    private static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            // Try to find a varargs method
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName) && isAssignable(paramTypes,
                    method.getParameterTypes(), method.isVarArgs())) {
                    return method;
                }
            }
            // If not found, try to find in superclass
            if (clazz.getSuperclass() != null) {
                return findMethod(clazz.getSuperclass(), methodName, paramTypes);
            }
        }
        return null;
    }

    private static boolean isAssignable(Class<?>[] paramTypes, Class<?>[] methodParamTypes,
        boolean isVarArgs) {
        if (isVarArgs) {
            if (paramTypes.length < methodParamTypes.length - 1) {
                return false;
            }
            for (int i = 0; i < methodParamTypes.length - 1; i++) {
                if (!methodParamTypes[i].isAssignableFrom(paramTypes[i])) {
                    return false;
                }
            }
            Class<?> varArgType = methodParamTypes[methodParamTypes.length - 1].getComponentType();
            for (int i = methodParamTypes.length - 1; i < paramTypes.length; i++) {
                if (!varArgType.isAssignableFrom(paramTypes[i])) {
                    return false;
                }
            }
            return true;
        } else {
            if (paramTypes.length != methodParamTypes.length) {
                return false;
            }
            for (int i = 0; i < paramTypes.length; i++) {
                if (!methodParamTypes[i].isAssignableFrom(paramTypes[i])) {
                    return false;
                }
            }
            return true;
        }
    }
}
