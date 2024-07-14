package com.cool.core.util;

import cn.hutool.core.io.file.PathUtil;
import com.cool.CoolApplication;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils {

    public static boolean isAbsolutePath(String pathStr) {
        Path path = Paths.get(pathStr);
        return path.isAbsolute();
    }

    public static String getUserDir() {
        return System.getProperty("user.dir");
    }

    public static String getModulesPath() {
        return getUserDir() + getSrcMainJava() + File.separator + CoolApplication.class.getPackageName()
            .replace(".", File.separator) + File.separator + "modules";
    }

    public static String getSrcMainJava() {
        return File.separator + "src" + File.separator + "main" + File.separator + "java";
    }
    public static String getTargetGeneratedAnnotations() {
        return "target" +  File.separator + "generated-sources" + File.separator + "annotations";
    }

    public static String getClassName(String filePath) {
        // 定位 "/src/main/java" 在路径中的位置
        int srcMainJavaIndex = filePath.indexOf(getSrcMainJava());
        if (srcMainJavaIndex == -1) {
            throw new IllegalArgumentException("File path does not contain 'src/main/java'");
        }

        // 提取 "src/main/java" 之后的路径
        // 将文件分隔符替换为包分隔符
        return filePath.substring(srcMainJavaIndex + ("src" + File.separator + "main" + File.separator + "java").length() + 2)
            .replace(File.separator, ".").replace(".java", "");
    }

    /**
     * 路径不存在创建
     */
    public static void noExistsMk(String pathStr) {
        Path path = Paths.get(pathStr);
        if (PathUtil.exists(path, false)) {
            PathUtil.mkParentDirs(path);
        }
    }
}
