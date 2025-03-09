package com.cool.modules.base.service.sys.impl;

import com.cool.core.exception.CoolPreconditions;
import com.cool.modules.base.dto.sys.CodeContentDto;
import com.cool.modules.base.service.sys.BaseCodingService;
import com.google.googlejavaformat.java.Formatter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BaseCodingServiceImpl implements BaseCodingService {

    @Value("${spring.profiles.active}")
    private String env;

    // 获取模块目录结构
    public List<String> getModuleTree() {
        if (!"local".equals(env)) {
            return List.of();  // 返回空列表
        }

        // 获取基础目录
        Path modulesPath = getModulesPath();
        // 获取模块文件夹
        try {
            return Files.list(modulesPath)
                    .filter(path -> !path.getFileName().toString().equals(".DS_Store"))
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getModulesPath() {
        String moduleDir = System.getProperty("user.dir"); // 可通过其他方式获取应用目录
        String packageName = BaseCodingServiceImpl.class.getPackageName();
        String modulesParentPath = packageName.split("modules")[0].replace(".", File.separator);
        return Paths.get(moduleDir, "src", "main", "java", modulesParentPath, "modules");
    }

    // 创建代码文件
    public void createCode(List<CodeContentDto> codes) {
        if (!"local".equals(env)) {
            throw new IllegalArgumentException("只能在开发环境下创建代码");
        }
        Path modulesPath = getModulesPath();
        String absolutePathStr = modulesPath.toAbsolutePath().toString();
        try {
            for (CodeContentDto code : codes) {
                // 格式化代码内容
                String formattedContent = formatContent(code.getContent());

                Path filePath = Paths.get(absolutePathStr, code.getPath().replace("src/modules", ""));
                Path dirPath = filePath.getParent();
                // 确保目录存在
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }
                // 写入文件
                try (FileWriter writer = new FileWriter(filePath.toFile())) {
                    writer.write(formattedContent);
                }
            }
        } catch (Exception e) {
            CoolPreconditions.alwaysThrow("生成代码失败", e);
        }
    }

    // 格式化代码内容
    public String formatContent(String content) {
        Formatter formatter = new Formatter();
        try {
            return formatter.formatSource(content);
        } catch (Exception ignored) {
        }
        return content;
    }
}
