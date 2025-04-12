package com.cool.core.plugin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.cool.core.config.PluginJson;
import com.cool.core.exception.CoolException;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.plugin.config.DynamicJarClassLoader;
import com.cool.core.plugin.event.PluginActionEnum;
import com.cool.core.plugin.event.PluginEventPublisher;
import com.cool.core.util.CoolPluginInvokers;
import com.cool.core.util.MapExtUtil;
import com.cool.core.util.PathUtils;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.service.PluginInfoService;
import com.mybatisflex.core.query.QueryWrapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 插件服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CoolPluginService {

    final private DynamicJarLoaderService dynamicJarLoaderService;

    final private PluginInfoService pluginInfoService;

    final private PluginEventPublisher pluginEventPublisher;

    @Value("${cool.plugin.path}")
    private String pluginPath;

    public void init() {
        List<PluginInfoEntity> list = pluginInfoService
            .list(QueryWrapper
                .create()
                .eq(PluginInfoEntity::getStatus, 1));
        if (ObjUtil.isEmpty(list)) {
            log.info("没有可初始化的插件");
            return;
        }
        list.forEach(this::initInstall);
    }

    /**
     * 系统启动初始化安装插件
     */
    private void initInstall(PluginInfoEntity entity) {
        PluginJson pluginJson = entity.getPluginJson();
        File file = new File(pluginJson.getJarPath());
        // 检查文件是否存在
        if (!file.exists()) {
            log.warn("插件文件不存在，请重新安装!");
            return;
        }
        file = new File(pluginJson.getJarPath());
        if (file.exists()) {
            ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                dynamicJarLoaderService.install(pluginJson.getJarPath(), true);
                // 设置配置
                CoolPluginInvokers.setPluginJson(entity.getKey(), entity);
                pluginEventPublisher.publish(entity.getKey(), PluginActionEnum.INSTALL, entity);
            } catch (Exception e) {
                log.error("初始化{}插件失败", entity.getName(), e);
            } finally {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
        }
    }

    /**
     * 安装jar
     */
    public void install(MultipartFile file, boolean force) {
        File jarFile = null;
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        String key = null;
        try {
            // 保存jar文件
            jarFile = saveJarFile(file);
            String jarFilePath = jarFile.getAbsolutePath();
            // 加载jar
            PluginJson pluginJson = dynamicJarLoaderService.install(jarFilePath, force);
            key = pluginJson.getKey();
            // 保存插件信息入库
            PluginInfoEntity pluginInfoEntity = savePluginInfo(pluginJson, jarFilePath, jarFile, force);
            // 把 ApplicationContext 对象传递打插件类中，使其在插件中也能正常使用spring bean对象
            CoolPluginInvokers.setApplicationContext(pluginJson.getKey());
            pluginEventPublisher.publish(pluginJson.getKey(), PluginActionEnum.INSTALL, pluginInfoEntity);
        } catch (PersistenceException persistenceException) {
            extractedAfterErr(jarFile, key);
            if (persistenceException.getMessage().contains("Duplicate entry")) {
                // 唯一键冲突
                CoolPreconditions.returnData(
                    new CoolPreconditions.ReturnData(1, "插件已存在，继续安装将覆盖"));
            }
            
            CoolPreconditions.alwaysThrow(persistenceException.getMessage());
        } catch (CoolException e) {
            extractedAfterErr(jarFile, key);
            throw e;
        } catch (Exception e) {
            log.error("插件安装失败", e);
            extractedAfterErr(jarFile, key);
            CoolPreconditions.alwaysThrow("插件安装失败", e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    private void extractedAfterErr(File jarFile, String key) {
        FileUtil.del(jarFile);
        if (ObjUtil.isNotEmpty(key)) {
            // 报错失败，调用卸载
            dynamicJarLoaderService.uninstall(key);
        }
    }

    /**
     * 保存jar文件
     */
    private File saveJarFile(MultipartFile file) throws IOException {
        String pathStr = pluginPath;
        if (!PathUtils.isAbsolutePath(pluginPath)) {
            // 相对路径
            pathStr = System.getProperty("user.dir") + File.separator + pluginPath;
        }
        // 将路径字符串转换为 Path 对象
        Path path = Paths.get(pathStr);
        // 检查路径是否存在
        if (!Files.exists(path)) {
            // 如果路径不存在，则创建目录（包括父目录）
            Files.createDirectories(path);
        }
        String jarFilePath =
            path + File.separator + System.currentTimeMillis() + "_" + file.getOriginalFilename() + ".jar";
        File jarFile = new File(jarFilePath);
        file.transferTo(jarFile);
        return jarFile;
    }

    /**
     * 卸载
     */
    public void uninstall(Long id) {
        PluginInfoEntity pluginInfoEntity = pluginInfoService.getPluginInfoEntityById(id);
        CoolPreconditions.checkEmpty(pluginInfoEntity, "插件不存在");
        dynamicJarLoaderService.uninstall(pluginInfoEntity.getKey());
        boolean flag = pluginInfoEntity.removeById();
        if (flag) {
            FileUtil.del(pluginInfoEntity.getPluginJson().getJarPath());
            pluginEventPublisher.publish(pluginInfoEntity.getKey(), PluginActionEnum.UNINSTALL, null);
        }
    }

    /**
     * 保存插件信息
     */
    private PluginInfoEntity savePluginInfo(PluginJson pluginJson, String jarFilePath ,
        File jarFile,
        boolean force) {
        CoolPreconditions.checkEmpty(pluginJson, "插件安装失败");
        pluginJson.setJarPath(jarFilePath);
        PluginInfoEntity pluginInfo = new PluginInfoEntity();
        BeanUtil.copyProperties(pluginJson, pluginInfo);
        setLogoOrReadme(pluginJson, pluginInfo);
        pluginInfo.setKey(pluginJson.getKey());
        pluginInfo.setPluginJson(pluginJson);
        if (force) {
            // 判断是否有同名插件， 有将其关闭
            closeSameNamePlugin(pluginJson);
            // 覆盖插件
            coverPlugin(pluginJson, pluginInfo);
            return pluginInfo;
        }
        pluginInfo.setStatus(1);
        pluginInfo.save();
        return pluginInfo;
    }

    /**
     * 覆盖插件
     */
    private void coverPlugin(PluginJson pluginJson, PluginInfoEntity pluginInfo) {
        // 通过key 找到id
        PluginInfoEntity one = pluginInfoService.getByKey(pluginJson.getKey());
        if (ObjUtil.isNotEmpty(one)) {
            String oldJarPath = one.getPluginJson().getJarPath();
            // 重新加载配置不更新
            pluginInfo.setConfig(one.getConfig());
            pluginInfo.getPluginJson().setConfig((Map<String, Object>) one.getConfig());
            // 设置插件配置
            CoolPluginInvokers.setPluginJson(pluginInfo.getKey(), pluginInfo);
            CopyOptions options = CopyOptions.create().setIgnoreNullValue(true);
            // 忽略无变更，无需更新的字段
            ignoreNoChange(pluginInfo, one);
            BeanUtil.copyProperties(pluginInfo, one, options);
            one.setStatus(1);
            if (one.updateById()) {
                // 覆盖时删除旧版本插件
                FileUtil.del(oldJarPath);
            }
        } else {
            pluginInfo.save();
        }
    }

    /**
     * 关闭同名插件
     */
    private void closeSameNamePlugin(PluginJson pluginJson) {
        if (ObjUtil.isNotEmpty(pluginJson.getSameHookId())) {
            // 存在同名，已强制安装，需将原插件关闭
            PluginInfoEntity sameHookPlugin = new PluginInfoEntity();
            sameHookPlugin.setStatus(0);
            sameHookPlugin.setId(pluginJson.getSameHookId());
            updatePlugin(sameHookPlugin);
        }
    }

    /**
     * 忽略无变更，无需更新的字段
     */
    private static void ignoreNoChange(PluginInfoEntity pluginInfo, PluginInfoEntity one) {
        if (ObjUtil.equals(pluginInfo.getLogo(), one.getLogo())) {
            // 头像没变，无需更新
            pluginInfo.setLogo(null);
        }
        if (ObjUtil.equals(pluginInfo.getReadme(), one.getReadme())) {
            // readme没变，无需更新
            pluginInfo.setReadme(null);
        }
    }

    /**
     * 设置logo或readme
     */
    private void setLogoOrReadme(PluginJson pluginJson, PluginInfoEntity pluginInfo) {
        if (ObjUtil.isNotEmpty(pluginJson.getLogo())) {
            DynamicJarClassLoader dynamicJarClassLoader = dynamicJarLoaderService
                .getDynamicJarClassLoader(pluginJson.getKey());
            InputStream inputStream = dynamicJarClassLoader.getResourceAsStream(
                pluginJson.getLogo());
            if (ObjUtil.isNotEmpty(inputStream)) {
                pluginInfo.setLogo(Base64Encoder.encode(IoUtil.readBytes(inputStream)));
            }
        }
        if (ObjUtil.isNotEmpty(pluginJson.getReadme())) {
            DynamicJarClassLoader dynamicJarClassLoader = dynamicJarLoaderService
                .getDynamicJarClassLoader(pluginJson.getKey());
            InputStream inputStream = dynamicJarClassLoader.getResourceAsStream(
                pluginJson.getReadme());
            if (ObjUtil.isNotEmpty(inputStream)) {
                pluginInfo.setReadme(StrUtil.str(IoUtil.readBytes(inputStream), "UTF-8"));
            }
        }
    }

    public void updatePlugin(PluginInfoEntity entity) {
        PluginInfoEntity dbPluginInfoEntity = pluginInfoService.getPluginInfoEntityById(
            entity.getId());
        // 调用插件更新配置标识
        boolean invokePluginConfig = false;
        if (!MapExtUtil.compareMaps((Map<String, Object>) entity.getConfig(),
            (Map<String, Object>) dbPluginInfoEntity.getConfig())) {
            // 不一致，说明更新了配置
            entity.setPluginJson(dbPluginInfoEntity.getPluginJson());
            entity.getPluginJson().setConfig((Map<String, Object>) entity.getConfig());
            // 更新了配置， 且插件是开启状态
            invokePluginConfig = ObjUtil.equals(dbPluginInfoEntity.getStatus(), 1);
        }
        if (!ObjUtil.equals(entity.getStatus(), dbPluginInfoEntity.getStatus())) {
            // 更新状态
            updateStatus(entity, dbPluginInfoEntity);
        }
        if (invokePluginConfig) {
            // 更新配置
            CoolPluginInvokers.setPluginJson(dbPluginInfoEntity.getKey(), entity);
        }
        pluginInfoService.update(entity);
        pluginEventPublisher.publish(dbPluginInfoEntity.getKey(), PluginActionEnum.UPDATE, pluginInfoService.getPluginInfoEntityById(
            entity.getId()));
    }

    /**
     * 更新插件状态
     */
    private void updateStatus(PluginInfoEntity entity, PluginInfoEntity dbPluginInfoEntity) {
        // 更新状态
        Integer status = entity.getStatus();
        if (ObjUtil.equals(status, 1)) {
            if (ObjUtil.isNotEmpty(dbPluginInfoEntity.getHook())) {
                // 查找是否有同名hook，有同名hook,如果状态为开启不允许在开启，需先关闭原来
                PluginInfoEntity hookPlugin = pluginInfoService
                    .getPluginInfoEntityByHook(dbPluginInfoEntity.getHook());
                if (ObjUtil.isNotEmpty(hookPlugin)) {
                    CoolPreconditions.check(
                        !ObjUtil.equals(hookPlugin.getKey(), dbPluginInfoEntity.getKey())
                            && ObjUtil.equals(hookPlugin.getStatus(), 1),
                        "插件已存在相同hook: {}，请选关闭{}插件,在开启当前插件（同名hook，只能有一个插件开启）",
                        hookPlugin.getHook(),
                        hookPlugin.getName());
                }
            }
            // 从关闭置为开启，触发重新加载jar
            initInstall(dbPluginInfoEntity);
        } else if (ObjUtil.equals(status, 0)) {
            // 插件关闭 卸载jar
            dynamicJarLoaderService.uninstall(dbPluginInfoEntity.getKey());
        }
    }

    /**
     * 通过hook获取插件
     */
    public PluginInfoEntity getPluginInfoEntityByHook(String hook) {
        return pluginInfoService.getPluginInfoEntityByHook(hook);
    }

    /**
     * 获取插件实例对象,插件未找到，抛出异常
     */
    public Object getInstance(String key) {
        return dynamicJarLoaderService.getBeanInstance(key);
    }

    /**
     * 获取插件实例对象,插件未找到，不抛出异常
     */
    public Object getInstanceWithoutCheck(String key) {
        return dynamicJarLoaderService.getInstanceWithoutCheck(key);
    }
}