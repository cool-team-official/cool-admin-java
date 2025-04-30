package com.cool.core.eps;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cool.core.annotation.EpsField;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.config.CustomOpenApiResource;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 实体信息与路径
 */
@Getter
@Component
@Slf4j
@RequiredArgsConstructor
public class CoolEps {

    @Value("${server.port}")
    private int serverPort;

    private Dict entityInfo;
    
    private Dict menuInfo;

    private JSONObject swaggerInfo;

    @Value("${springdoc.api-docs.enabled:false}")
    private boolean apiDocsEnabled;

    public Dict admin;

    public Dict app;

    final private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Async
    public void init() {
        if (!apiDocsEnabled) {
            log.info("服务启动成功，端口：{}", serverPort);
            return;
        }
        entityInfo = Dict.create();
        menuInfo = Dict.create();
        swaggerInfo = swaggerInfo();
        Runnable task = () -> {
            entity();
            urls();
            log.info("初始化eps完成，服务启动成功，端口：{}", serverPort);
        };
        // ThreadUtil.safeSleep(3000);
        ThreadUtil.execute(task);
    }

    /**
     * 清空所有的数据
     */
    public void clear() {
        admin = Dict.create();
        app = Dict.create();
    }

    /**
     * 构建所有的url
     */
    private void urls() {
        Dict admin = Dict.create();
        Dict app = Dict.create();
        ArrayList<Object> emptyList = new ArrayList<>();
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> methodEntry : map.entrySet()) {
            RequestMappingInfo info = methodEntry.getKey();
            HandlerMethod method = methodEntry.getValue();
            TokenIgnore tokenIgnore = method.getMethodAnnotation(TokenIgnore.class);
            String module = getModule(method);
            if (StrUtil.isNotEmpty(module)) {
                String entityName = getEntity(method.getBeanType());
                String methodPath = getMethodUrl(method);
                String escapedMethodPath = methodPath.replace("{", "\\{").replace("}", "\\}");
                String prefix = Objects.requireNonNull(getUrl(info))
                        .replaceFirst("(?s)(.*)" + escapedMethodPath, "$1");
                Dict result;
                int type = 0;
                if (prefix.startsWith("/admin")) {
                    result = admin;
                } else if (prefix.startsWith("/app")) {
                    result = app;
                    type = 1;
                } else {
                    continue;
                }
                if (result.get(module) == null) {
                    result.set(module, new ArrayList<Dict>());
                }

                List<Dict> urls = result.getBean(module);
                Dict item = CollUtil.findOne(urls, dict -> {
                    if (dict != null) {
                        return dict.getStr("module").equals(module)
                            && dict.getStr("controller")
                            .equals(method.getBeanType().getSimpleName());
                    } else {
                        return false;
                    }
                });
                if (item != null) {
                    item.set("api", apis(prefix, methodPath, item.getBean("api"), tokenIgnore));
                } else {
                    item = Dict.create();
                    item.set("controller", method.getBeanType().getSimpleName());
                    item.set("module", module);
                    item.set("info", Dict.create().set("type",
                            Dict.create()
                                    .set("name", getLastPathSegment(prefix))
                                    .set("description", "")
                    ));
                    item.set("api", apis(prefix, methodPath, item.getBean("api"), tokenIgnore));
                    item.set("name", entityName);
                    item.set("columns", entityInfo.get(entityName));
                    item.set("pageQueryOp", Dict.create().set("keyWordLikeFields", emptyList)
                                                         .set("fieldEq", emptyList)
                                                         .set("fieldLike", emptyList));
                    item.set("prefix", prefix);
                    item.set("menu", menuInfo.get(entityName));
                    urls.add(item);
                }
                if (type == 0) {
                    admin.set(module, urls);
                }
                if (type == 1) {
                    app.set(module, urls);
                }

            }
        }
        this.admin = admin;
        this.app = app;

    }
    /**
     * 提取URL路径中的最后一个路径段
     * 示例：输入 "/api/getData" 返回 "getData"
     */
    private String getLastPathSegment(String url) {
        if (StrUtil.isBlank(url)) {
            return "";
        }

        int queryIndex = url.indexOf('?');
        if (queryIndex != -1) {
            url = url.substring(0, queryIndex);
        }

        int slashIndex = url.lastIndexOf('/');
        if (slashIndex != -1 && slashIndex < url.length() - 1) {
            return url.substring(slashIndex + 1);
        } else {
            return url;
        }
    }


    /**
     * 设置所有的api
     *
     * @param prefix     路由前缀
     * @param methodPath 方法路由
     * @param list       api列表
     * @return api列表
     */
    private List<Dict> apis(String prefix, String methodPath, List<Dict> list, TokenIgnore tokenIgnore) {
        if (ObjUtil.isNull(list)) {
            list =  new ArrayList<>();
        }
        Dict item = Dict.create();
        item.set("path", methodPath);
        item.set("tag", "");
        item.set("dts", Dict.create());
        item.set("ignoreToken", false);
        if (tokenIgnore != null) {
            item.set("ignoreToken", true);
        }
        setSwaggerInfo(item, prefix + methodPath);
        list.add(item);
        return list;
    }

    /**
     * 设置swagger相关信息
     *
     * @param item 信息载体
     * @param url  url地址
     */
    private void setSwaggerInfo(Dict item, String url) {
        JSONObject paths = swaggerInfo.getJSONObject("paths");
        JSONObject urlInfo = paths.getJSONObject(url);
        String method = urlInfo.keySet().iterator().next();
        JSONObject methodInfo = urlInfo.getJSONObject(method);
        item.set("method", method);
        item.set("summary", methodInfo.getStr("summary"));
    }

    /**
     * 获得方法的url地址
     *
     * @param handlerMethod 方法
     * @return 方法url地址
     */
    private String getMethodUrl(HandlerMethod handlerMethod) {
        String url = "";
        Method method = handlerMethod.getMethod();
        Annotation[] annotations = method.getDeclaredAnnotations();

        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType.getName().contains("org.springframework.web.bind.annotation")) {
                Map<String, Object> attributes = Arrays.stream(annotationType.getDeclaredMethods())
                    .collect(Collectors.toMap(Method::getName, m -> {
                        try {
                            return m.invoke(annotation);
                        } catch (Exception e) {
                            throw new IllegalStateException("Failed to access annotation attribute",
                                e);
                        }
                    }));

                if (attributes.containsKey("value") && ObjUtil.isNotEmpty(attributes.get("value"))) {
                    url = ((String[]) attributes.get("value"))[0];
                }
                break;
            }
        }

        return url;
    }

    /**
     * 获得url地址
     *
     * @param info 路由信息
     * @return url地址
     */
    private String getUrl(RequestMappingInfo info) {
        if (info.getPathPatternsCondition() == null) {
            return null;
        }
        Set<String> paths = info.getPathPatternsCondition().getPatternValues();
        return paths.iterator().next();
    }

    /**
     * 获得模块
     *
     * @param method 方法
     * @return 模块
     */
    private String getModule(HandlerMethod method) {
        String beanName = method.getBeanType().getName();
        String[] beanNames = beanName.split("[.]");
        int index = ArrayUtil.indexOf(beanNames, "modules");
        if (index > 0) {
            return beanNames[index + 1];
        }
        return null;
    }

    /**
     * 获得swagger的json信息
     */
    private JSONObject swaggerInfo() {
        try {
            byte[] bytes = SpringUtil.getBean(CustomOpenApiResource.class).getOpenApiJson();
            return JSONUtil.parseObj(new String(bytes));
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    /**
     * 获得Controller上的实体类型
     *
     * @param controller Controller类
     * @return 实体名称
     */
    private String getEntity(Class<?> controller) {
        try {
            ParameterizedType parameterizedType = (ParameterizedType) controller.getGenericSuperclass();
            Class<?> entityClass = (Class<?>) parameterizedType.getActualTypeArguments()[1];
            return entityClass.getSimpleName();
        } catch (Exception e) {
            return "";
        }
    }

    private void entity() {
        // 扫描所有的实体类
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation("", Table.class);
        classes.forEach(e -> {
            // 获得属性
            Field[] fields = getAllDeclaredFields(e);
            List<Dict> columns = columns(fields);
            entityInfo.set(e.getSimpleName(), columns);


            Table mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(e, Table.class);

            menuInfo.set(e.getSimpleName(), mergedAnnotation.comment());

                    
        });
    }

    /**
     * 获取类及其所有父类中声明的字段
     *
     * @param clazz 要检查的类
     * @return 包含类及其所有父类中声明的所有字段的数组
     */
    public static Field[] getAllDeclaredFields(Class<?> clazz) {
        // 参数校验
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = clazz;

        // 循环遍历类及其父类
        while (currentClass != null) {
            Field[] declaredFields = currentClass.getDeclaredFields();
            allFields.addAll(Arrays.asList(declaredFields));
            currentClass = currentClass.getSuperclass();
        }

        // 将列表转换为数组返回
        return allFields.toArray(new Field[0]);
    }

    /**
     * 获得所有的列
     *
     * @param fields 字段名
     * @return 所有的列
     */
    private List<Dict> columns(Field[] fields) {
        List<Dict> dictList = new ArrayList<>();
        for (Field field : fields) {
            Dict dict = Dict.create();
            
            EpsField epsField = AnnotatedElementUtils.findMergedAnnotation(field, EpsField.class);
            if (epsField != null) {
                dict.set("component", epsField.component());
            }
            
            ColumnDefine columnInfo = AnnotatedElementUtils.findMergedAnnotation(field, ColumnDefine.class);
            if (columnInfo == null) {
                continue;
            }
            dict.set("comment", columnInfo.comment());
            dict.set("length", columnInfo.length());
            dict.set("propertyName", field.getName());
            dict.set("type", matchType(field.getType().getName()));
            dict.set("nullable", !columnInfo.notNull());
            dict.set("source", "a." + field.getName());
            dictList.add(dict);
        }
        return dictList;
    }

    /**
     * java类型转换成JavaScript对应的类型
     *
     * @param type 类型
     * @return JavaScript类型
     */
    private String matchType(String type) {
        return switch (type) {
            case "java.lang.Boolean" -> "boolean";
            case "java.lang.Long", "java.lang.Integer", "java.lang.Short", "java.lang.Float",
                 "java.lang.Double" -> "number";
            case "java.util.Date" -> "date";
            default -> "string";
        };
    }
}
