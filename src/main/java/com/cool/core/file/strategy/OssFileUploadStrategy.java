package com.cool.core.file.strategy;

import com.cool.core.config.FileModeEnum;
import com.cool.core.file.UpLoadModeType;
import com.cool.core.util.CoolPluginInvokers;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component("ossFileUploadStrategy")
public class OssFileUploadStrategy implements FileUploadStrategy {

    @Override
    public Object upload(MultipartFile[] files, HttpServletRequest request, PluginInfoEntity pluginInfoEntity)
            throws IOException {
        return CoolPluginInvokers.invokePlugin(pluginInfoEntity.getKey());
    }

    @Override
    public UpLoadModeType getMode() {
        return new UpLoadModeType(FileModeEnum.CLOUD);
    }
}
