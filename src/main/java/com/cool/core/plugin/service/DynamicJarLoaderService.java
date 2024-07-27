package com.cool.core.plugin.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cool.core.config.PluginJson;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.plugin.config.DynamicJarClassLoader;
import com.cool.core.util.AnnotationUtils;
import com.cool.core.util.CompilerUtils;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.service.PluginInfoService;
import java.io.File;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 动态加载jar包
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicJarLoaderService {

    final private PluginInfoService pluginInfoService;

    private final Map<String, DynamicJarClassLoader> dynamicJarClassLoaderMap = new ConcurrentHashMap<>();

    private final Map<String, Object> pluginMap = new ConcurrentHashMap<>();

    public PluginJson install(String jarFilePath, boolean force) throws Exception {
        URL jarUrl = new URL("jar:file:" + new File(jarFilePath).getAbsolutePath() + "!/");
        DynamicJarClassLoader dynamicJarClassLoader = new DynamicJarClassLoader(new URL[]{jarUrl},
            Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(dynamicJarClassLoader);
        PluginJson pluginJson = getPluginJsonAndCheck(force, dynamicJarClassLoader);
        // 加载类
        List<Class<?>> plugins = new ArrayList<>();
        int count = 0;
        uninstall(pluginJson.getKey());
        Instant start = Instant.now();
        int progressThreshold = 10; // 输出进度的阈值为10%
        try (JarFile jarFile = ((JarURLConnection) jarUrl.openConnection()).getJarFile()) {
            List<JarEntry> list = jarFile.stream().toList();
            int size = list.size();
            int currentProgress = 0;
            for (JarEntry jarEntry : list) {
                count++;
                loadClass(jarEntry, dynamicJarClassLoader, plugins);
                // 计算进度百分比
                int progress = (int) ((count / (double) size) * 100);
                // 输出一次进度
                if (progress % progressThreshold == 0 && currentProgress != progress) {
                    log.info("安装进度: {}%", progress);
                    currentProgress = progress;
                }
            }
        } finally{
            Instant end = Instant.now();
            Duration timeElapsed = Duration.between(start, end);
            log.info("本次共加载{}个文件 耗时: {}ms", count, timeElapsed.toMillis());
        }
        // 校验插件
        checkPlugin(plugins);
        registerPlugin(pluginJson.getKey(), plugins.get(0), dynamicJarClassLoader, force);
        dynamicJarClassLoaderMap.put(pluginJson.getKey(), dynamicJarClassLoader);

        log.info("插件{}安装成功.", pluginJson.getKey());
        return pluginJson;
    }

    /**
     * 校验，获取插件配置
     */
    private PluginJson getPluginJsonAndCheck(boolean force, DynamicJarClassLoader dynamicJarClassLoader) {
        InputStream inputStream = dynamicJarClassLoader.getResourceAsStream("plugin.json");
        CoolPreconditions.check(ObjUtil.isEmpty(inputStream), "不合规插件：未找到plugin.json文件");
        String pluginJsonStr = StrUtil.str(IoUtil.readBytes(inputStream), "UTF-8");
        PluginJson pluginJson = JSONUtil.toBean(pluginJsonStr, PluginJson.class);
        CoolPreconditions.check(ObjUtil.isEmpty(pluginJson.getKey()), "该插件缺少唯一标识");
        if (!force) {
            PluginInfoEntity byKey = pluginInfoService.getByKey(pluginJson.getKey());
            if (ObjUtil.isNotEmpty(byKey)) {
                CoolPreconditions.returnData(
                    new CoolPreconditions.ReturnData(1, "插件已存在，继续安装将覆盖"));
            }
        }
        if (ObjUtil.isNotEmpty(pluginJson.getHook())) {
            // 查找hook是否已经存在,提示是否要替换，原hook将关闭
            PluginInfoEntity pluginInfoEntity = pluginInfoService
                .getPluginInfoEntityByHook(pluginJson.getHook());
            if (!force) {
                CoolPreconditions.returnData(
                    ObjUtil.isNotEmpty(pluginInfoEntity)
                        && !ObjUtil.equals(pluginInfoEntity.getKey(), pluginJson.getKey()),
                    new CoolPreconditions.ReturnData(1,
                        "插件已存在相同hook: {}，继续安装将关闭原来插件（同名hook，只能有一个状态开启）",
                        pluginJson.getHook()));

            } else if (ObjUtil.isNotEmpty(pluginInfoEntity)
                && !ObjUtil.equals(pluginInfoEntity.getKey(), pluginJson.getKey())) {
                // 存在同名hook，需将原hook修改为关闭
                pluginJson.setSameHookId(pluginInfoEntity.getId());
            }
        }
        return pluginJson;
    }

    /**
     * 加载class
     */
    private static void loadClass(JarEntry jarEntry,
        DynamicJarClassLoader dynamicJarClassLoader, List<Class<?>> plugins)
        throws ClassNotFoundException {

        String entryName = jarEntry.getName();
        if (!entryName.endsWith(".class")) {
            return;
        }
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
            Class<?> clazz = dynamicJarClassLoader.loadClass(className);
            if (AnnotationUtils.hasCoolPluginAnnotation(clazz)) {
                plugins.add(clazz);
            }
        } catch (NoClassDefFoundError | UnsupportedClassVersionError ignored) {
        }
    }

    /**
     * 注册插件
     */
    private void registerPlugin(String key, Class<?> pluginClazz,
        DynamicJarClassLoader dynamicJarClassLoader,
        boolean force) {
        if (!force && pluginMap.containsKey(key)) {
            dynamicJarClassLoader.unload();
            CoolPreconditions.returnData(
                new CoolPreconditions.ReturnData(1, "插件已存在，继续安装将覆盖"));
        }
        if (ObjUtil.isNotEmpty(key)) {
            pluginMap.remove(key);
            pluginMap.put(key, ReflectUtil.newInstance(pluginClazz));
        }
    }

    /**
     * 卸载
     */
    public boolean uninstall(String key) {
        DynamicJarClassLoader dynamicJarClassLoader = getDynamicJarClassLoader(key);
//        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        log.info("插件{}开始卸载", key);
        try {
//            Thread.currentThread().setContextClassLoader(dynamicJarClassLoader);
            pluginMap.remove(key);
            if (dynamicJarClassLoader != null) {
                dynamicJarClassLoader.unload();
            }
        } catch (Exception e) {
            log.error("uninstall {}失败", key, e);
            CoolPreconditions.alwaysThrow("卸载失败");
        } finally {
//            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
        log.info("插件{}卸载完成", key);
        return true;
    }

    /**
     * 校验插件是否合法
     */
    private void checkPlugin(List<Class<?>> plugins) {
        CoolPreconditions.checkEmpty(plugins, "未找到插件程序");
        int size = plugins.size();
        CoolPreconditions.check(size == 0,
            "没找到符合规范的插件,插件需有@CoolPlugin注解，且以Plugin结尾(如：DemoPlugin)");
        CoolPreconditions.check(size > 1,
            "识别到当前安装包有多个插件(只能有一个@CoolPlugin注解)，一次只支持一个插件导入");
    }

    /**
     * 获取插件实例对象
     */
    public Object getBeanInstance(String key) {
        CoolPreconditions.checkEmpty(key, "插件key is null");
        if (pluginMap.containsKey(key)) {
            return pluginMap.get(key);
        }
        CoolPreconditions.alwaysThrow("插件 {} 未找到", key);
        return null;
    }

    /**
     * 获取自定义类加载器
     */
    public DynamicJarClassLoader getDynamicJarClassLoader(String key) {
        return dynamicJarClassLoaderMap.get(key);
    }
}
