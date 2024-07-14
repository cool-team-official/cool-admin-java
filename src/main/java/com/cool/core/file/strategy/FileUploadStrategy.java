package com.cool.core.file.strategy;

import com.cool.core.file.UpLoadModeType;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface FileUploadStrategy {

    /**
     * 文件上传
     */
    Object upload(MultipartFile[] files, HttpServletRequest request, PluginInfoEntity pluginInfoEntity)
            throws IOException;

    /**
     * 文件上传模式
     *
     * @return 上传模式
     */
    UpLoadModeType getMode();

    default boolean isAbsolutePath(String pathStr) {
        Path path = Paths.get(pathStr);
        return path.isAbsolute();
    }

    default String getExtensionName(String fileName) {
        if (fileName.contains(".")) {
            String[] names = fileName.split("[.]");
            return "." + names[names.length - 1];
        }
        return "";
    }
}
