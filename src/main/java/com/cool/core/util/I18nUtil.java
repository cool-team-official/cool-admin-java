package com.cool.core.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
@Component
public class I18nUtil {

    public static final String MSG_PREFIX = "msg_";
    public static final String MENU_PREFIX = "menu_";
    public static final String DICT_INFO_PREFIX = "dictInfo_";
    public static final String DICT_TYPE_PREFIX = "dictType_";

    public static boolean enable = false;

    public static String path;
    public static String getLanguage() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return (String) attributes.getAttribute("cool-language", RequestAttributes.SCOPE_REQUEST);
    }

    private static final Map<String, JSONObject> data = new ConcurrentHashMap<>();

    private void load(String key, File file) {
        try {
            String content = FileUtil.readUtf8String(file);
            data.put(key, JSONUtil.parseObj(content));
        } catch (Exception e) {
            log.error("读取国际化文件失败", e);
        }
    }

    public boolean exist(String name) {
        // 获取该目录下所有的 .json 文件
        List<File> jsonFiles = FileUtil.loopFiles(getPath(), file ->
            file.isFile() && file.getName().endsWith(".json")
        );
        AtomicReference<Boolean> flag = new AtomicReference<>(false);
        jsonFiles.forEach(file -> {
            String parentName = file.getParentFile().getName();
            String key = parentName + "_" + file.getName().replace(".json", "");
            if (key.equals(name)) {
                flag.set(true);
                // 加载
                load(key, file);
            }
        });
        return flag.get();
    }

    public static String getI18nMenu(String name) {
        return getI18n(name, MENU_PREFIX);
    }

    public static String getI18nMsg(String name) {
        return getI18n(name, MSG_PREFIX);
    }

    public static String getI18nDictInfo(String name) {
        return getI18n(name, DICT_INFO_PREFIX);
    }
    public static String getI18nDictType(String name) {
        return getI18n(name, DICT_TYPE_PREFIX);
    }
    private static String getI18n(String name, String prefix) {
        if (!enable) {
            return name;
        }
        String language = I18nUtil.getLanguage();
        if (language == null) {
            return name;
        }
        JSONObject jsonObject = data.get(prefix + language);
        if (jsonObject == null) {
            return name;
        }
        String str = jsonObject.getStr(name);
        if (str == null) {
            return name;
        }
        return str;
    }

    public void update(String key, JSONObject object) {
        data.put(key, object);
        String[] split = key.split("_");
        String absolutePath = getPath();
        File file = FileUtil.file(absolutePath, split[0], split[1] + ".json");
        // 确保父目录存在
        FileUtil.mkParentDirs(file);
        // 写入内容
        FileUtil.writeUtf8String(JSONUtil.toJsonStr(object), file);
    }

    private String getPath() {
        String absolutePath = path;
        if (!PathUtils.isAbsolutePath(absolutePath)) {
            absolutePath = PathUtils.getUserDir() + File.separator + absolutePath;
        }
        return absolutePath;
    }

    public void clear() {
        data.clear();
        List<File> jsonFiles = FileUtil.loopFiles(getPath(), file ->
            file.isFile() && file.getName().endsWith(".json")
        );
        jsonFiles.forEach(File::delete);
        enable = false;
    }
}