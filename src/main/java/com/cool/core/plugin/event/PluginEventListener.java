package com.cool.core.plugin.event;

import static com.cool.core.plugin.consts.PluginConsts.i18n;

import cn.hutool.core.util.ObjUtil;
import com.cool.core.i18n.I18nGenerator;
import com.cool.core.util.I18nUtil;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PluginEventListener implements ApplicationListener<PluginEvent> {
    private final I18nGenerator i18nGenerator;
    private final I18nUtil i18nUtil;
    @Async
    @Override
    public void onApplicationEvent(PluginEvent event) {
        if (ObjUtil.equals(event.getKey(), i18n)) {
            // 国际化插件变更
            PluginActionEnum actionEnum = event.getActionEnum();
            PluginInfoEntity pluginInfoEntity = event.getPluginInfoEntity();

            if (ObjUtil.equals(actionEnum, PluginActionEnum.INSTALL) && ObjUtil.equals(pluginInfoEntity.getStatus(), 1)) {
                // 安装插件后，如果插件状态为启用，则生成国际化文件
                i18nGenerator.run((Map<String, Object>) pluginInfoEntity.getConfig());
            } else if (ObjUtil.equals(actionEnum, PluginActionEnum.UPDATE)) {
                if (ObjUtil.equals(pluginInfoEntity.getStatus(), 1)) {
                    // 更新插件配置
                    i18nGenerator.run((Map<String, Object>) pluginInfoEntity.getConfig());
                } else {
                    // 停用
                    I18nUtil.enable = false;
                }
            } else if (ObjUtil.equals(actionEnum, PluginActionEnum.UNINSTALL)) {
                // 卸载国际化插件，则删除国际化文件
                i18nUtil.clear();
            }
        }
    }
}

