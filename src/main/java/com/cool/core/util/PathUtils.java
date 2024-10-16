package com.cool.core.util;

import cn.hutool.core.io.file.PathUtil;
import cn.hutool.core.text.AntPathMatcher;
import com.cool.CoolApplication;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PathUtils {
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

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

    /**
     * 判断给定的请求URI是否匹配列表中的任意一个URL模式
     * 使用Ant风格的路径匹配来处理URL模式，提供了一种通配符匹配的方法
     *
     * @param urls 待匹配的URL模式列表
     * @param requestURI 请求的URI
     * @return 如果请求URI匹配列表中的任意一个URL模式，则返回true；否则返回false
     */
    public static boolean isMatch(List<String> urls, String requestURI) {
        return urls.stream()
            .anyMatch(url -> antPathMatcher.match(url, requestURI));
    }
}
