package com.cool.core.i18n;

import static com.cool.core.util.I18nUtil.*;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cool.core.lock.CoolLock;
import com.cool.core.util.CoolPluginInvokers;
import com.cool.core.util.I18nUtil;
import com.cool.core.util.PathUtils;
import com.cool.modules.base.entity.sys.BaseSysMenuEntity;
import com.cool.modules.base.service.sys.BaseSysMenuService;
import com.cool.modules.dict.entity.DictInfoEntity;
import com.cool.modules.dict.entity.DictTypeEntity;
import com.cool.modules.dict.service.DictInfoService;
import com.cool.modules.dict.service.DictTypeService;
import com.mybatisflex.core.query.QueryWrapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class I18nGenerator {
    private final BaseSysMenuService baseSysMenuService;
    private final DictTypeService dictTypeService;
    private final DictInfoService dictInfoService;
    private final CoolLock coolLock;
    private final I18nUtil i18nUtil;

    private List<String> languages;
    private static final Duration DURATION = Duration.ofSeconds(30);
    public void run(Map<String, Object> map) {
        log.info("国际化 翻译...");
        languages = (List<String>) map.getOrDefault("languages", List.of("zh-cn", "zh-tw", "en"));
        path = (String) map.getOrDefault("path", "assets/i18n");
        init();
        log.info("✅国际化 翻译 成功！！！");
        enable = true;
    }

    public void init() {
        // 四个任务并发执行
        CompletableFuture<Void> futureMsg = CompletableFuture.runAsync(this::genBaseMsg);
        CompletableFuture<Void> futureMenu = CompletableFuture.runAsync(this::genBaseMenu);
        CompletableFuture<Void> futureDictInfo = CompletableFuture.runAsync(this::genBaseDictInfo);
        CompletableFuture<Void> futureDictType = CompletableFuture.runAsync(this::genBaseDictType);

        // 等待全部执行完成
        CompletableFuture.allOf(futureMsg, futureMenu, futureDictInfo, futureDictType).join();
    }

    private void genBaseMsg() {
        try {
            Map<String, String> msgMap = new HashMap<>();
            // 从idea本地启动时，从项目目录中读取
            Files.walk(Paths.get(System.getProperty("user.dir")))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .filter(path -> !path.toString().contains("/target/") && !path.toString().contains("/.git/"))
                .forEach(path -> msgMap.putAll(processFile(path)));
            if (ObjUtil.isNotEmpty(msgMap)) {
                // 系统异常信息,输出到resources/i18n 文件夹下，只有本地运行会生成
                File msgfile = FileUtil.file(PathUtils.getUserDir(),
                    "src", "main", "resources", "cool", "i18n", "msg", "template.json");
                // 确保父目录存在
                FileUtil.mkParentDirs(msgfile);
                // 写入内容
                FileUtil.writeUtf8String(JSONUtil.toJsonStr(msgMap), msgfile);
            } else {
                try {
                    // jar启动时，从jar包中读取
                    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                    Resource resource = resolver.getResource("classpath:cool/i18n/msg/template.json");
                    String content = FileUtil.readUtf8String(resource.getFile());
                    msgMap.putAll(JSONUtil.toBean(content, Map.class));
                } catch (Exception e) {
                    log.error("获取系统异常信息失败", e);
                }
            }
            extracted(MSG_PREFIX, msgMap);
        } catch (Exception e) {
            log.error("国际化系统异常信息失败", e);
        }
    }

    /**
     * 生成菜单信息国际化
     */
    @Async
    public void asyncGenBaseMenu() {
        if (coolLock.tryLock(MENU_PREFIX, DURATION)) {
            genBaseMenu();
            coolLock.unlock(MENU_PREFIX);
        }
    }
    private void genBaseMenu() {
        try {
            Map<String, String> menuMap = baseSysMenuService.list(QueryWrapper.create().select(BaseSysMenuEntity::getName))
                .stream()
                .collect(Collectors.toMap(
                    BaseSysMenuEntity::getName,
                    BaseSysMenuEntity::getName,
                    (oldValue, newValue) -> oldValue
                ));
            extracted(MENU_PREFIX, menuMap);
        } catch (Exception e) {
            log.error("国际化菜单信息失败", e);
        }
    }
    @Async
    public void asyncGenBaseDictType() {
        if (coolLock.tryLock(DICT_TYPE_PREFIX, DURATION)) {
            genBaseDictType();
            coolLock.unlock(DICT_TYPE_PREFIX);
        }
    }
    private void genBaseDictType() {
        try {
            Map<String, String> dataMap = dictTypeService.list(QueryWrapper.create().select(DictTypeEntity::getName))
                .stream()
                .collect(Collectors.toMap(
                    DictTypeEntity::getName,
                    DictTypeEntity::getName,
                    (oldValue, newValue) -> oldValue
                ));
            extracted(DICT_TYPE_PREFIX, dataMap);
        } catch (Exception e) {
            log.error("国际化字段类型信息失败", e);
        }
    }
    @Async
    public void asyncGenBaseDictInfo() {
        if (coolLock.tryLock(DICT_INFO_PREFIX, DURATION)) {
            genBaseDictInfo();
            coolLock.unlock(DICT_INFO_PREFIX);
        }
    }
    private void genBaseDictInfo() {
        try {
            Map<String, String> dataMap = dictInfoService.list(QueryWrapper.create().select(DictInfoEntity::getName))
                .stream()
                .collect(Collectors.toMap(
                    DictInfoEntity::getName,
                    DictInfoEntity::getName,
                    (oldValue, newValue) -> oldValue
                ));
            extracted(DICT_INFO_PREFIX, dataMap);
        } catch (Exception e) {
            log.error("国际化字段类型信息失败", e);
        }
    }

    private void extracted(String prefix, Map<String, String> dataMap) {
        languages.forEach(language -> {
            String key = prefix + language;
            if (!i18nUtil.exist(key)) {
                JSONObject jsonObject = invokeTranslate(dataMap, language);
                if (ObjUtil.isNotNull(jsonObject)) {
                    i18nUtil.update(key, jsonObject);
                }
            }
        });
    }

    private JSONObject invokeTranslate(Map<String, String> map, String language) {
        return (JSONObject) CoolPluginInvokers.invoke("i18n", "invokeTranslate", map, language);
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