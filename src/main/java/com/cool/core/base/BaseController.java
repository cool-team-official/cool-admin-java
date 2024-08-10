package com.cool.core.base;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.request.CrudOption;
import com.cool.core.request.R;
import com.cool.core.util.SpringContextUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 控制层基类
 *
 * @param <S>
 * @param <T>
 */
public abstract class BaseController<S extends BaseService<T>, T extends BaseEntity<T>> {

    @Getter
    protected S service;
    protected Class<T> entityClass;

    @PostConstruct
    private void init() {
        if (entityClass == null) {
            this.entityClass = currentEntityClass();
        }
        if (service == null) {
            this.service = (S) SpringContextUtils.getBean(currentServiceClass());
        }
    }

    private final String COOL_PAGE_OP = "COOL_PAGE_OP";
    private final String COOL_LIST_OP = "COOL_LIST_OP";

    private final ThreadLocal<CrudOption<T>> pageOption = new ThreadLocal<>();
    private final ThreadLocal<CrudOption<T>> listOption = new ThreadLocal<>();
    private final ThreadLocal<JSONObject> requestParams = new ThreadLocal<>();

    @ModelAttribute
    protected void preHandle(HttpServletRequest request,
        @RequestAttribute JSONObject requestParams) {
        this.pageOption.set(new CrudOption<>(requestParams));
        this.listOption.set(new CrudOption<>(requestParams));
        this.requestParams.set(requestParams);
        init(request, requestParams);
        request.setAttribute(COOL_PAGE_OP, this.pageOption.get());
        request.setAttribute(COOL_LIST_OP, this.listOption.get());
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
                .set("ids", service.addBatch(requestParams, array.toList(currentEntityClass()))));
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
        JSONObject info = JSONUtil.parseObj(JSONUtil.toJsonStr(service.info(id)));
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
    protected R info(@RequestAttribute() JSONObject requestParams,
        @RequestParam() Long id) {
        return R.ok(service.info(requestParams, id));
    }

    /**
     * 列表查询
     *
     * @param requestParams 请求参数
     */
    @Operation(summary = "查询", description = "查询多个信息")
    @PostMapping("/list")
    protected R list(@RequestAttribute() JSONObject requestParams,
        @RequestAttribute(COOL_LIST_OP) CrudOption<T> option) {
        return R.ok(service.list(requestParams, option.getQueryWrapper(entityClass)));
    }

    /**
     * 分页查询
     *
     * @param requestParams 请求参数
     */
    @Operation(summary = "分页", description = "分页查询多个信息")
    @PostMapping("/page")
    protected R page(@RequestAttribute() JSONObject requestParams,
        @RequestAttribute(COOL_PAGE_OP) CrudOption<T> option) {
        Integer page = requestParams.getInt("page", 1);
        Integer size = requestParams.getInt("size", 20);
        return R.ok(
            pageResult((Page<T>) service.page(requestParams, new Page<>(page, size),
                option.getQueryWrapper(entityClass))));
    }

    /**
     * 分页结果
     *
     * @param page 分页返回数据
     */
    protected Map<String, Object> pageResult(Page<T> page) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("size", page.getPageSize());
        pagination.put("page", page.getPageNumber());
        pagination.put("total", page.getTotalRow());
        result.put("list", page.getRecords());
        result.put("pagination", pagination);
        return result;
    }

    public Class<T> currentEntityClass() {
        // 使用  获取泛型参数类型
        Type type = TypeUtil.getTypeArgument(this.getClass(), 1); // 获取第二个泛型参数
        if (type instanceof Class<?>) {
            return (Class<T>) type;
        }
        throw new IllegalStateException("Unable to determine entity class type");
    }

    public Class<T> currentServiceClass() {
        // 使用  获取泛型参数类型
        Type type = TypeUtil.getTypeArgument(this.getClass(), 0); // 获取第一个泛型参数
        if (type instanceof Class<?>) {
            return (Class<T>) type;
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

}