package com.cool.core.plugin.config;

import com.cool.core.exception.CoolPreconditions;
import com.cool.core.util.AnnotationUtils;
import com.cool.core.util.CompilerUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.net.URLClassLoader;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;

/**
 * * 自定义类加载器
 */
@Slf4j
public class DynamicJarClassLoader extends URLClassLoader {

    private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

    public DynamicJarClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    private Boolean lock = false;

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
        CoolPreconditions.check(lock, "异步加载任务还未完成，请稍后重试......");
        lock = true;
        new Thread(() -> {
            try {
                for (Map.Entry<String, Class<?>> entry : loadedClasses.entrySet()) {
                    // 从已加载的类集合中移除该类
                    String className = entry.getKey();
                    loadedClasses.remove(className);
                }
                // 从其父类加载器的加载器层次结构中移除该类加载器
                close();
            } catch (Exception e) {
                log.error("unload error", e);
            } finally{
                lock = false;
            }
        }).start();
    }
    public void loadClass(List<JarEntry> jarEntries, List<Class<?>> plugins) {
        for (JarEntry jarEntry : jarEntries) {
            loadClass(jarEntry, plugins);
        }
    }
    /**
     * 加载class
     */
    public void loadClass(JarEntry jarEntry, List<Class<?>> plugins) {
        String entryName = jarEntry.getName();
        String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
        if (entryName.startsWith(CompilerUtils.META_INF_VERSIONS)) {
            // 处理多版本类
            String jdkVersion = CompilerUtils.getJdkVersion();
            if (!entryName.startsWith(CompilerUtils.META_INF_VERSIONS + jdkVersion)) {
                return;
            }
            // 替换版本目录
            className = className.replace((CompilerUtils.META_INF_VERSIONS + jdkVersion).replace("/", ".") + ".", "");
        }
        try {
            // 加载类
            Class<?> clazz = super.loadClass(className);
            if (plugins != null && AnnotationUtils.hasCoolPluginAnnotation(clazz)) {
                // 添加插件
                plugins.add(clazz);
            }
        } catch (ClassNotFoundException e) {
            log.error("loadClassErr", e);
        } catch ( NoClassDefFoundError | UnsupportedClassVersionError ignored) {
        }
    }

    /**
     * 异步加载
     */
    public void asyncLoadClass(String key, List<JarEntry> list) {
        CoolPreconditions.check(lock, "异步加载任务还未完成，请稍后重试......");
        lock = true;
        new Thread(() -> {
            log.info("开始异步加载插件{}依赖类....", key);
            Instant start = Instant.now();
            int size = list.size();
            int currentProgress = 0;
            int progressThreshold = 10; // 输出进度的阈值为10%
            int count = 0;
            try{
                for (JarEntry jarEntry : list) {
                    count++;
                    loadClass(jarEntry, null);
                    // 计算进度百分比
                    int progress = (int) ((count / (double) size) * 100);
                    // 输出一次进度
                    if (progress % progressThreshold == 0 && currentProgress != progress) {
                        log.info("插件{}依赖类异步加载进度: {}%", key, progress);
                        currentProgress = progress;
                    }
                }
            } finally{
                Instant end = Instant.now();
                Duration timeElapsed = Duration.between(start, end);
                log.info("异步加载插件{}依赖类完成，共加载{}个文件 耗时: {}ms", key, count, timeElapsed.toMillis());
                lock = false;
            }
        }).start();
    }
}
