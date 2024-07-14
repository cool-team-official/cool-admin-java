package com.cool.modules.task.run;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.cool.core.exception.CoolException;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 执行定时任务
 */
public class ScheduleRunnable implements Runnable {
    private final Object target;
    private final Method method;
    private final Object[] params;

    public ScheduleRunnable(String beanName, String methodName, String params)
            throws NoSuchMethodException, SecurityException {
        this.target = SpringUtil.getBean(beanName);

        if (StrUtil.isNotBlank(params)) {
            String[] paramArray = params.split(",");
            this.params = new Object[paramArray.length];
            Class<?>[] paramTypes = new Class<?>[paramArray.length];

            for (int i = 0; i < paramArray.length; i++) {
                String param = paramArray[i].trim();
                if (param.matches("-?\\d+")) {
                    this.params[i] = Integer.parseInt(param);
                    paramTypes[i] = int.class;
                } else if (param.matches("-?\\d+L")) {
                    this.params[i] = Long.parseLong(param.substring(0, param.length() - 1));
                    paramTypes[i] = long.class;
                } else if (param.matches("-?\\d+\\.\\d+")) {
                    this.params[i] = Double.parseDouble(param);
                    paramTypes[i] = double.class;
                } else if (param.matches("-?\\d+\\.\\d+f")) {
                    this.params[i] = Float.parseFloat(param.substring(0, param.length() - 1));
                    paramTypes[i] = float.class;
                } else if (param.equalsIgnoreCase("true") || param.equalsIgnoreCase("false")) {
                    this.params[i] = Boolean.parseBoolean(param);
                    paramTypes[i] = boolean.class;
                } else if (param.length() == 1) {
                    this.params[i] = param.charAt(0);
                    paramTypes[i] = char.class;
                } else {
                    // Remove leading and trailing quotation marks for string parameters
                    if (param.startsWith("\"") && param.endsWith("\"")) {
                        param = param.substring(1, param.length() - 1);
                    }
                    this.params[i] = param;
                    paramTypes[i] = String.class;
                }
            }

            this.method = findMethod(target.getClass(), methodName, paramTypes);
        } else {
            this.params = new Object[0];
            this.method = target.getClass().getDeclaredMethod(methodName);
        }
    }

    private Method findMethod(Class<?> targetClass, String methodName, Class<?>[] paramTypes) throws NoSuchMethodException {
        try {
            return targetClass.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            // Try with wrapper classes
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i] == int.class) {
                    paramTypes[i] = Integer.class;
                } else if (paramTypes[i] == long.class) {
                    paramTypes[i] = Long.class;
                } else if (paramTypes[i] == double.class) {
                    paramTypes[i] = Double.class;
                } else if (paramTypes[i] == float.class) {
                    paramTypes[i] = Float.class;
                } else if (paramTypes[i] == boolean.class) {
                    paramTypes[i] = Boolean.class;
                } else if (paramTypes[i] == char.class) {
                    paramTypes[i] = Character.class;
                }
            }
            return targetClass.getDeclaredMethod(methodName, paramTypes);
        }
    }

    @Override
    public void run() {
        try {
            ReflectionUtils.makeAccessible(method);
            if (params.length > 0) {
                method.invoke(target, params);
            } else {
                method.invoke(target);
            }
        } catch (Exception e) {
            throw new CoolException("执行定时任务失败", e);
        }
    }
}
