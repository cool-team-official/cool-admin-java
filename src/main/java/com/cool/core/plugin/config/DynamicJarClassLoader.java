package com.cool.core.plugin.config;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * * 自定义类加载器
 */
@Slf4j
public class DynamicJarClassLoader extends URLClassLoader {

    private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

    public DynamicJarClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 从已加载的类集合中获取指定名称的类
        Class<?> clazz = loadedClasses.get(name);
        if (clazz != null) {
            return clazz;
        }
        clazz = super.findClass(name);
        // 将加载的类添加到已加载的类集合中
        loadedClasses.put(name, clazz);
        return clazz;
    }

    public void unload() {
        try {
            for (Map.Entry<String, Class<?>> entry : loadedClasses.entrySet()) {
                // 从已加载的类集合中移除该类
                String className = entry.getKey();
                loadedClasses.remove(className);
                try {
                    // 调用该类的destory方法，回收资源
                    Class<?> clazz = entry.getValue();
                    Method destory = clazz.getDeclaredMethod("destory");
                    destory.invoke(clazz);
                } catch (NoClassDefFoundError | Exception ignored) {
                }
            }
            // 从其父类加载器的加载器层次结构中移除该类加载器
            close();
        } catch (Exception e) {
            log.error("unload error", e);
        }
    }
}