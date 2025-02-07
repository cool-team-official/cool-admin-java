package com.cool.core.file.strategy;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.cool.core.config.FileModeEnum;
import com.cool.core.config.LocalFileProperties;
import com.cool.core.exception.CoolException;
import com.cool.core.exception.CoolPreconditions;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component("localFileUploadStrategy")
@RequiredArgsConstructor
public class LocalFileUploadStrategy implements FileUploadStrategy {

    final private LocalFileProperties localFileProperties;

    /**
     * 上传文件
     *
     * @param files 上传的文件
     * @return 文件路径
     */
    @Override
    public Object upload(MultipartFile[] files, HttpServletRequest request,
        PluginInfoEntity pluginInfoEntity) {
        CoolPreconditions.check(StrUtil.isEmpty(localFileProperties.getBaseUrl()),
            "filePath 或 baseUrl 未配置");
        try {
            List<String> fileUrls = new ArrayList<>();
            String baseUrl = localFileProperties.getBaseUrl();
            String date = DateUtil.format(new Date(),
                DatePattern.PURE_DATE_PATTERN);
            String absoluteUploadFolder = localFileProperties.getAbsoluteUploadFolder();
            String fullPath = absoluteUploadFolder + "/" + date;
            FileUtil.mkdir(fullPath);
            for (MultipartFile file : files) {
                // 保存文件
                String fileName = StrUtil.uuid().replaceAll("-", "") + getExtensionName(
                    Objects.requireNonNull(file.getOriginalFilename()));
                file.transferTo(new File(fullPath
                    + "/" + fileName));
                fileUrls.add(baseUrl + "/" + date + "/" + fileName);
            }
            if (fileUrls.size() == 1) {
                return fileUrls.get(0);
            }
            return fileUrls;
        } catch (Exception e) {
            throw new CoolException("文件上传失败", e);
        }
    }

    /**
     * 文件上传模式
     *
     * @return 上传模式
     */
    public Map<String, String> getMode(String key) {
        return Map.of("mode", FileModeEnum.LOCAL.value(),
            "type", FileModeEnum.LOCAL.type());
    }
}
