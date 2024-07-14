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
import com.cool.core.util.CoolPluginInvokers;
import com.cool.core.util.MapExtUtil;
import com.cool.core.util.PathUtils;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.service.PluginInfoService;
import com.mybatisflex.core.query.QueryWrapper;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Value("${cool.pluginPath}")
    private String pluginPath;

    public void init() {
        List<PluginInfoEntity> list = pluginInfoService
            .list(QueryWrapper
                .create().select(PluginInfoEntity::getId, PluginInfoEntity::getPluginJson,
                    PluginInfoEntity::getKey, PluginInfoEntity::getName)
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
        // 检查路径是否存在
        if (!file.exists()) {
            PluginInfoEntity pluginInfoEntity = pluginInfoService.getById(entity.getId());
            FileUtil.writeBytes(pluginInfoEntity.getJarFile(), file);
        }
        file = new File(pluginJson.getJarPath());
        if (file.exists()) {
            ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                dynamicJarLoaderService.install(pluginJson.getJarPath(), true);
                // 设置配置
                CoolPluginInvokers.setPluginJson(entity.getKey(), entity);
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
        String fileName;
        File jarFile = null;
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            String pathStr = pluginPath;
            if (!PathUtils.isAbsolutePath(pluginPath)) {
                // 相对路径
                pathStr = System.getProperty("user.dir") + "/" + pluginPath;
            }
            // 将路径字符串转换为 Path 对象
            Path path = Paths.get(pathStr);
            // 检查路径是否存在
            if (!Files.exists(path)) {
                // 如果路径不存在，则创建目录（包括父目录）
                Files.createDirectories(path);
            }
            fileName =
                path + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename() + ".jar";
            jarFile = new File(fileName);
            file.transferTo(jarFile);
            // 加载jar
            PluginJson pluginJson = dynamicJarLoaderService.install(fileName, force);
            // 保存插件信息入库
            savePluginInfo(pluginJson, fileName, jarFile, force);
            // 把 ApplicationContext 对象传递打插件类中，使其在插件中也能正常使用spring bean对象
            CoolPluginInvokers.setApplicationContext(pluginJson.getKey());
        } catch (CoolException e) {
            FileUtil.del(jarFile);
            throw e;
        } catch (Exception e) {
            log.error("插件安装失败", e);
            CoolPreconditions.alwaysThrow("插件安装失败", e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    /**
     * 卸载
     */
    public void uninstall(Long id) {
        PluginInfoEntity pluginInfoEntity = pluginInfoService.getPluginInfoEntityByIdNoJarFile(id);
        CoolPreconditions.checkEmpty(pluginInfoEntity, "插件不存在");
        if (dynamicJarLoaderService.uninstall(pluginInfoEntity.getKey())) {
            boolean flag = pluginInfoEntity.removeById();
            if (flag) {
                FileUtil.del(pluginInfoEntity.getPluginJson().getJarPath());
            }
        }
    }

    /**
     * 保存插件信息
     */
    private void savePluginInfo(PluginJson pluginJson, String fileName, File jarFile,
        boolean force) {
        CoolPreconditions.checkEmpty(pluginJson, "插件安装失败");
        pluginJson.setJarPath(fileName);
        PluginInfoEntity pluginInfo = new PluginInfoEntity();
        BeanUtil.copyProperties(pluginJson, pluginInfo);
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
        pluginInfo.setKey(pluginJson.getKey());
        pluginInfo.setPluginJson(pluginJson);
        // 转二进制
        pluginInfo.setJarFile(FileUtil.readBytes(jarFile));

        if (force) {
            CopyOptions options = CopyOptions.create().setIgnoreNullValue(true);
            if (ObjUtil.isNotEmpty(pluginJson.getSameHookId())) {
                // 存在同名，已强制安装，需将原插件关闭
                PluginInfoEntity sameHookPlugin = new PluginInfoEntity();
                sameHookPlugin.setStatus(0);
                sameHookPlugin.setId(pluginJson.getSameHookId());
                updatePlugin(sameHookPlugin);
            }
            // 通过key 找到id
            PluginInfoEntity one = pluginInfoService.getByKeyNoJarFile(pluginJson.getKey());
            if (ObjUtil.isNotEmpty(one)) {
                // 重新加载配置不更新
                pluginInfo.setConfig(one.getConfig());
                pluginInfo.getPluginJson().setConfig(one.getConfig());
                BeanUtil.copyProperties(pluginInfo, one, options);
                one.updateById();
                // 重新安装了调用设置插件历史配置信息
                CoolPluginInvokers.setPluginJson(pluginJson.getKey(), one);
                return;
            }
        }
        pluginInfo.save();
    }

    public void updatePlugin(PluginInfoEntity entity) {
        PluginInfoEntity dbPluginInfoEntity = pluginInfoService.getPluginInfoEntityByIdNoJarFile(
            entity.getId());
        boolean updateConfig = false;
        if (!MapExtUtil.compareMaps(entity.getConfig(), dbPluginInfoEntity.getConfig())) {
            // 不一致，说明更新了配置
            entity.getPluginJson().setConfig(entity.getConfig());
            updateConfig = true;
        }
        if (!ObjUtil.equals(entity.getStatus(), dbPluginInfoEntity.getStatus())) {
            // 更新状态
            updateConfig = updateStatus(entity, dbPluginInfoEntity, updateConfig);
        }
        if (updateConfig) {
            // 更新配置
            CoolPluginInvokers.setPluginJson(getKeyById(entity.getId()), entity);
        }
        pluginInfoService.update(entity);
    }

    /**
     * 更新插件状态
     */
    private boolean updateStatus(PluginInfoEntity entity, PluginInfoEntity dbPluginInfoEntity,
        boolean updateConfig) {
        // 更新状态
        Integer status = entity.getStatus();
        if (ObjUtil.equals(status, 1)) {
            if (ObjUtil.isNotEmpty(dbPluginInfoEntity.getHook())) {
                // 查找是否有同名hook，有同名hook,如果状态为开启不允许在开启，需先关闭原来
                PluginInfoEntity hookPlugin = pluginInfoService
                    .getPluginInfoEntityByHookNoJarFile(dbPluginInfoEntity.getHook());
                if (ObjUtil.isNotEmpty(hookPlugin)) {
                    CoolPreconditions.check(
                        !ObjUtil.equals(hookPlugin.getKey(), dbPluginInfoEntity.getKey())
                            && ObjUtil.equals(hookPlugin.getStatus(), 1),
                        "插件已存在相同hook: {}，请选关闭{}插件,在开启当前插件（同名hook，只能有一个插件开启）",
                        hookPlugin.getHook(),
                        hookPlugin.getName());
                }
            }
            // 充关闭置为开启，触发重新加载jar
            initInstall(dbPluginInfoEntity);
            updateConfig = false;
        } else if (ObjUtil.equals(status, 0)) {
            // 插件关闭 卸载jar
            dynamicJarLoaderService.uninstall(dbPluginInfoEntity.getKey());
        }
        return updateConfig;
    }

    /**
     * 通过hook获取插件
     */
    public PluginInfoEntity getPluginInfoEntityByHook(String hook) {
        return pluginInfoService.getPluginInfoEntityByHookNoJarFile(hook);
    }

    /**
     * 通过id 获取key
     */
    private String getKeyById(Long id) {
        PluginInfoEntity one = pluginInfoService.getPluginInfoEntityByIdNoJarFile(id);
        if (ObjUtil.isNotEmpty(one)) {
            return one.getKey();
        }
        return null;
    }

    /**
     * 获取插件实例对象
     */
    public Object getInstance(String key) {
        return dynamicJarLoaderService.getBeanInstance(key);
    }
}
