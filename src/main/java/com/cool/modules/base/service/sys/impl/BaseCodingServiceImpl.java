package com.cool.modules.base.service.sys.impl;

import com.cool.core.exception.CoolPreconditions;
import com.cool.modules.base.dto.sys.CodeContentDto;
import com.cool.modules.base.service.sys.BaseCodingService;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
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
        List<String> list = Lists.newArrayList();
        try {
            for (CodeContentDto code : codes) {
                // 格式化代码内容
                String formattedContent = code.getContent();
                Path filePath = Paths.get(absolutePathStr, code.getPath().replace("java/", "/"));
                Path dirPath = filePath.getParent();
                // 确保目录存在
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }
                // 写入文件
                try (FileWriter writer = new FileWriter(filePath.toFile())) {
                    formattedContent = formattedContent.replace("com.tangzc.mybatisflex.autotable.annotation.Index;",
                            "org.dromara.autotable.annotation.Index;");
                    writer.write(formattedContent);
                }
                list.add(filePath.toString());
            }
        } catch (Exception e) {
            CoolPreconditions.alwaysThrow("生成代码失败", e);
        }
        log.info("代码已生成，请先编译后，手动重启服务");
    }
}