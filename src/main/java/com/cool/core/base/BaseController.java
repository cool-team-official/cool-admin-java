package com.cool.core.base;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cool.core.enums.QueryModeEnum;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.request.CrudOption;
import com.cool.core.request.PageResult;
import com.cool.core.request.R;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 控制层基类
 *
 * @param <S>
 * @param <T>
 */
public abstract class BaseController<S extends BaseService<T>, T extends BaseEntity<T>> {

    @Getter
    @Autowired
    protected S service;
    protected Class<T> entityClass;

    protected final String COOL_PAGE_OP = "COOL_PAGE_OP";
    protected final String COOL_LIST_OP = "COOL_LIST_OP";
    protected final String COOL_INFO_OP = "COOL_INFO_OP";

    private final ThreadLocal<CrudOption<T>> pageOption = new ThreadLocal<>();
    private final ThreadLocal<CrudOption<T>> listOption = new ThreadLocal<>();
    private final ThreadLocal<CrudOption<T>> infoOption = new ThreadLocal<>();
    private final ThreadLocal<JSONObject> requestParams = new ThreadLocal<>();

    @ModelAttribute
    protected void preHandle(HttpServletRequest request,
        @RequestAttribute JSONObject requestParams) {
        String requestPath = ((ServletRequestAttributes) Objects.requireNonNull(
            RequestContextHolder.getRequestAttributes())).getRequest().getRequestURI();
        if (!requestPath.endsWith("/page") && !requestPath.endsWith("/list")
            && !requestPath.endsWith("/info")) {
            // 非page或list不执行
            return;
        }
        this.pageOption.set(new CrudOption<>(requestParams));
        this.listOption.set(new CrudOption<>(requestParams));
        this.infoOption.set(new CrudOption<>(requestParams));
        this.requestParams.set(requestParams);
        init(request, requestParams);
        request.setAttribute(COOL_PAGE_OP, this.pageOption.get());
        request.setAttribute(COOL_LIST_OP, this.listOption.get());
        request.setAttribute(COOL_INFO_OP, this.infoOption.get());

        removeThreadLocal();
    }

    /**
     * 手动移除变量
     */
    private void removeThreadLocal() {
        this.listOption.remove();
        this.pageOption.remove();
        this.requestParams.remove();
    }

    public CrudOption<T> createOp() {
        return new CrudOption<>(this.requestParams.get());
    }

    public void setInfoOption(CrudOption<T> infoOption) {
        this.infoOption.set(infoOption);
    }

    public void setListOption(CrudOption<T> listOption) {
        this.listOption.set(listOption);
    }

    public void setPageOption(CrudOption<T> pageOption) {
        this.pageOption.set(pageOption);
    }

    protected abstract void init(HttpServletRequest request, JSONObject requestParams);

    /**
     * 新增
     * <p>
     * // * @param t 实体对象
     */
    @Operation(summary = "新增", description = "新增信息，对应后端的实体类")
    @PostMapping("/add")
    protected R add(@RequestAttribute() JSONObject requestParams) {
        String body = requestParams.getStr("body");
        if (JSONUtil.isTypeJSONArray(body)) {
            JSONArray array = JSONUtil.parseArray(body);
            return R.ok(Dict.create()
                .set("id", service.addBatch(requestParams, array.toList(currentEntityClass()))));
        } else {
            return R.ok(Dict.create().set("id",
                service.add(requestParams, requestParams.toBean(currentEntityClass()))));
        }
    }

    /**
     * 删除
     *
     * @param params 请求参数 ids 数组 或者按","隔开
     */
    @Operation(summary = "删除", description = "支持批量删除 请求参数 ids 数组 或者按\",\"隔开")
    @PostMapping("/delete")
    protected R delete(HttpServletRequest request, @RequestBody Map<String, Object> params,
        @RequestAttribute() JSONObject requestParams) {
        service.delete(requestParams, Convert.toLongArray(getIds(params)));
        return R.ok();
    }

    /**
     * 修改
     *
     * @param t 修改对象
     */
    @Operation(summary = "修改", description = "根据ID修改")
    @PostMapping("/update")
    protected R update(@RequestBody T t, @RequestAttribute() JSONObject requestParams) {
        Long id = t.getId();
        JSONObject info = JSONUtil.parseObj(JSONUtil.toJsonStr(service.getById(id)));
        requestParams.forEach(info::set);
        info.set("updateTime", new Date());
        service.update(requestParams, JSONUtil.toBean(info, currentEntityClass()));
        return R.ok();
    }

    /**
     * 信息
     *
     * @param id ID
     */
    @Operation(summary = "信息", description = "根据ID查询单个信息")
    @GetMapping("/info")
    protected R<T> info(@RequestAttribute() JSONObject requestParams,
        @RequestParam() Long id,
        @RequestAttribute(COOL_INFO_OP) CrudOption<T> option) {
        T info = (T) service.info(requestParams, id);
        invokerTransform(option, info);
        return R.ok(info);
    }

    /**
     * 列表查询
     *
     * @param requestParams 请求参数
     */
    @Operation(summary = "查询", description = "查询多个信息")
    @PostMapping("/list")
    protected R<List<T>> list(@RequestAttribute() JSONObject requestParams,
        @RequestAttribute(COOL_LIST_OP) CrudOption<T> option) {
        QueryModeEnum queryModeEnum = option.getQueryModeEnum();
        List list = (List) switch (queryModeEnum) {
            case ENTITY_WITH_RELATIONS -> service.listWithRelations(requestParams, option.getQueryWrapper(currentEntityClass()));
            case CUSTOM -> transformList(service.list(requestParams, option.getQueryWrapper(currentEntityClass()), option.getAsType()), option.getAsType());
            default -> service.list(requestParams, option.getQueryWrapper(currentEntityClass()));
        };
        invokerTransform(option, list);
        return R.ok(list);
    }

    /**
     * 分页查询
     *
     * @param requestParams 请求参数
     */
    @Operation(summary = "分页", description = "分页查询多个信息")
    @PostMapping("/page")
    protected R<PageResult<T>> page(@RequestAttribute() JSONObject requestParams,
        @RequestAttribute(COOL_PAGE_OP) CrudOption<T> option) {
        Integer page = requestParams.getInt("page", 1);
        Integer size = requestParams.getInt("size", 20);
        QueryModeEnum queryModeEnum = option.getQueryModeEnum();
        Object obj = switch (queryModeEnum) {
            case ENTITY_WITH_RELATIONS -> service.pageWithRelations(requestParams, new Page<>(page, size), option.getQueryWrapper(currentEntityClass()));
            case CUSTOM -> transformPage(service.page(requestParams, new Page<>(page, size), option.getQueryWrapper(currentEntityClass()), option.getAsType()), option.getAsType());
            default -> service.page(requestParams, new Page<>(page, size), option.getQueryWrapper(currentEntityClass()));
        };
        Page pageResult = (Page) obj;
        invokerTransform(option, pageResult.getRecords());
        return R.ok(pageResult(pageResult));
    }

    /**
     * 转换参数，组装数据
     */
    private void invokerTransform(CrudOption<T> option, Object obj) {
        if (ObjUtil.isNotEmpty(option.getTransform())) {
            if (obj instanceof List) {
                ((List)obj).forEach(o -> {
                    option.getTransform().apply(o);
                });
            } else {
                option.getTransform().apply(obj);
            }
        }
    }

    /**
     * 分页结果
     *
     * @param page 分页返回数据
     */
    protected PageResult<T> pageResult(Page<T> page) {
        return PageResult.of(page);
    }

    public Class<T> currentEntityClass() {
        if (entityClass != null) {
            return this.entityClass;
        }
        // 使用  获取泛型参数类型
        Type type = TypeUtil.getTypeArgument(this.getClass(), 1); // 获取第二个泛型参数
        if (type instanceof Class<?>) {
            entityClass = (Class<T>) type;
            return entityClass;
        }
        throw new IllegalStateException("Unable to determine entity class type");
    }

    protected List<Long> getIds(Map<String, Object> params) {
        Object ids = params.get("ids");
        CoolPreconditions.checkEmpty(ids, "ids 参数错误");
        if (!(ids instanceof ArrayList)) {
            ids = ids.toString().split(",");
        }
        return Convert.toList(Long.class, ids);
    }

    /**
     * 适用于自定义返回值为 map，map 的key为数据库字段，转驼峰命名
     */
    protected List transformList(List records, Class<?> asType) {
        if (ObjUtil.isEmpty(asType) || !Map.class.isAssignableFrom(asType)) {
            return records;
        }
        List<Map> list = new ArrayList<>();
        Editor<String> keyEditor = property -> StrUtil.toCamelCase(property);
        records.forEach(o ->
            list.add(BeanUtil.beanToMap(o, new HashMap(), false, keyEditor)));
        return list;
    }
    protected Page transformPage(Page page, Class<?> asType) {
        page.setRecords(transformList(page.getRecords(), asType));
        return page;
    }
}