package com.cool.core.config;

import com.cool.core.util.PathUtils;
import java.io.File;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cool.file.local")
public class LocalFileProperties {

    // 跟域名
    private String baseUrl;

    private String uploadPath = "assets/public/upload";

    public String getAbsoluteUploadFolder() {
        if (!PathUtils.isAbsolutePath(uploadPath)) {
            // 相对路径
            return System.getProperty("user.dir") + File.separator + uploadPath;
        }
        // 绝对路径
        return uploadPath;
    }
}
