package com.cool.core.util;

import cn.hutool.core.io.file.PathUtil;
import com.cool.CoolApplication;
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
        return getUserDir() + "/src/main/java/" + CoolApplication.class.getPackageName()
            .replace(".", "/") + "/modules";
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
