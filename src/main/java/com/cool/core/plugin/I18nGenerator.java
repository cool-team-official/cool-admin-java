package com.cool.core.plugin;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class I18nGenerator {
    public static void main(String[] args) {
        new I18nGenerator().run();
    }
    public void run() {
        System.out.println("i18n translate ...");
        // 要生成的文件路径
        File msgfile = FileUtil.file(System.getProperty("user.dir"),
                "src", "main", "resources", "cool", "i18n", "msg", "en.json");
        if (!msgfile.exists()) {
            JSONObject jsonObject = genExceptionMsg();

            // 确保父目录存在
            FileUtil.mkParentDirs(msgfile);
            // 写入内容
            FileUtil.writeUtf8String(JSONUtil.toJsonStr(jsonObject), msgfile);
        }
        // 要生成的文件路径
        File menufile = FileUtil.file(System.getProperty("user.dir"),
                "src", "main", "resources", "cool", "i18n", "menu", "en.json");
        if (!menufile.exists()) {
            JSONObject jsonObject = genBaseMenu();
            // 确保父目录存在
            FileUtil.mkParentDirs(menufile);
            // 写入内容
            FileUtil.writeUtf8String(JSONUtil.toJsonStr(jsonObject), menufile);
        }
        System.out.println("✅i18n translate success ！！！");
    }

    /**
     * 生成菜单信息国际化
     */
    private JSONObject genBaseMenu() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:cool/data/menu/*.json");
            Map<String, String> map = new HashMap<>();
            List<String> list = new ArrayList<>();
            // 遍历所有.json文件
            for (Resource resource : resources) {
                String jsonStr = IoUtil.read(resource.getInputStream(), StandardCharsets.UTF_8);
                // 使用 解析 JSON 字符串
                JSONArray jsonArray = JSONUtil.parseArray(jsonStr);
                // 遍历 JSON 数组
                for (Object obj : jsonArray) {
                    JSONObject jsonObj = (JSONObject) obj;
                    parseMenu(jsonObj, list);
                }
            }
            for (String value : list) {
                map.put(value, value);
            }
            return invokeTranslate(map, "en");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseMenu(JSONObject jsonObj, List<String> list) {
        list.add(jsonObj.getStr("name"));
        // 递归处理子菜单
        JSONArray childMenus = jsonObj.getJSONArray("childMenus");
        if (childMenus != null) {
            for (Object obj : childMenus) {
                JSONObject childObj = (JSONObject) obj;
                parseMenu(childObj, list);
            }
        }
    }

    /**
     * 生成异常信息国际化
     */
    private JSONObject genExceptionMsg() {
        Path rootPath = Paths.get(System.getProperty("user.dir"));
        try {
            Map<String, String> map = new HashMap<>();
            Files.walk(rootPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !path.toString().contains("/target/") && !path.toString().contains("/.git/"))
                    .forEach(path -> map.putAll(processFile(path)));
            return invokeTranslate(map, "en");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private JSONObject invokeTranslate(Map<String, String> map, String language) {
        if (map.isEmpty()) {
            return new JSONObject();
        }
        Map<String, Object> data = new HashMap<>();
        data.put("label", "i18n-node");
        data.put("params", Map.of("text", JSONUtil.toJsonStr(map), "language", language));
        data.put("stream", false);
        String res = HttpUtil.post("https://service.cool-js.com/api/open/flow/run/invoke", JSONUtil.toJsonStr(data));
        JSONObject jsonObject = JSONUtil.parseObj(res);
        return jsonObject.getJSONObject("data").getJSONObject("result").getJSONObject("data");
    }

    // 匹配 CoolPreconditions 抛异常语句中的中文字符串
    private static final Pattern EXCEPTION_PATTERN = Pattern.compile(
            "CoolPreconditions\\.(\\w+)\\s*\\([^;]*?\"([^\"]*[\u4e00-\u9fa5]+[^\"]*)\"", Pattern.MULTILINE
    );

    private static Map<String, String> processFile(Path path) {
        Map<String, String> map = new HashMap<>();
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            // 去掉注释
            content = removeComments(content);

            // 仅查找方法体内的 CoolPreconditions 调用
            Matcher matcher = EXCEPTION_PATTERN.matcher(content);
            while (matcher.find()) {
                String chineseText = matcher.group(2).trim();
                map.put(chineseText, chineseText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    // 移除注释（单行与多行）
    private static String removeComments(String code) {
        String noMultiLine = code.replaceAll("/\\*.*?\\*/", ""); // 多行注释
        return noMultiLine.replaceAll("//.*", ""); // 单行注释
    }
}
