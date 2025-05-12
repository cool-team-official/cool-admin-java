package com.cool.core.request;


import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.cool.core.enums.QueryModeEnum;
import com.cool.core.util.ConvertUtil;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.core.env.Environment;

/**
 * 查询构建器
 *
 * @param <T>
 */
@Data
public class CrudOption<T> {

    private QueryWrapper queryWrapper;
    private QueryColumn[] fieldEq;
    private QueryColumn[] keyWordLikeFields;
    private QueryColumn[] select;
    private JSONObject requestParams;

    private QueryModeEnum queryModeEnum;

    private Transform<Object> transform;

    public interface Transform<B> {
        void apply(B obj);
    }

    /**
     * queryModeEnum 为 CUSTOM,可设置 默认为Map
     */
    private Class<?> asType;

    private Environment evn;

    public CrudOption(JSONObject requestParams) {
        this.requestParams = requestParams;
        this.queryWrapper = QueryWrapper.create();
        this.evn = SpringUtil.getBean(Environment.class);
        queryModeEnum = QueryModeEnum.ENTITY;
    }

    public QueryWrapper getQueryWrapper(Class<T> entityClass) {
        return build(this.queryWrapper, entityClass);
    }

    public CrudOption<T> queryWrapper(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
        return this;
    }

    /**
     * 按前端传上来的字段值做eq
     */
    public CrudOption<T> fieldEq(QueryColumn... fields) {
        this.fieldEq = fields;
        return this;
    }

    /**
     * 按前端传上来的字段值做like
     */
    public CrudOption<T> keyWordLikeFields(QueryColumn... fields) {
        this.keyWordLikeFields = fields;
        return this;
    }

    /**
     * 需要返回给前端的字段
     */
    public CrudOption<T> select(QueryColumn... selects) {
        this.select = selects;
        return this;
    }

    /**
     * 查询模式决定返回值
     * 目前有三种模式，按实体查询返回、关联查询返回(实体字段上加 @RelationOneToMany 等注解)、自定义返回结果
     */
    public CrudOption<T> queryModeEnum(QueryModeEnum queryModeEnum) {
        this.queryModeEnum = queryModeEnum;
        if (ObjUtil.equal(queryModeEnum, QueryModeEnum.CUSTOM)
                && ObjUtil.isEmpty(asType)) {
            asType = Map.class;
        }
        return this;
    }

    /**
     * 自定义返回结果对象类型
     */
    public CrudOption<T> asType(Class<?> asType) {
        this.asType = asType;
        return this;
    }

    /**
     * 转换参数，组装数据
     */
    public CrudOption<T> transform(Transform<Object> transform) {
        this.transform = transform;
        return this;
    }

    /**
     * 构建查询条件
     *
     * @return QueryWrapper
     */
    private QueryWrapper build(QueryWrapper queryWrapper, Class<T> entityClass) {
        if (ObjectUtil.isNotEmpty(fieldEq)) {
            Arrays.stream(fieldEq).toList().forEach(filed -> {
                String filedName = StrUtil.toCamelCase(filed.getName());
                Object obj = requestParams.get(filedName);
                if (ObjUtil.isEmpty(obj)) {
                    return;
                }
                if (obj instanceof JSONArray) {
                    // 集合
                    queryWrapper.and(filed.in(ConvertUtil.covertListByClass(filedName, (JSONArray)obj, entityClass).toArray()));
                } else {
                    // 对象
                    queryWrapper.and(filed.eq(ConvertUtil.convertByClass(filedName, obj, entityClass)));
                }
            });
        }
        if (ObjectUtil.isNotEmpty(this.keyWordLikeFields)) {
            Object keyWord = requestParams.get("keyWord");
            if (ObjectUtil.isEmpty(keyWord)) {
                // // keyWord值为空，遍历keyWordLikeFields字段，根据queryColumn字段名构建查询条件
                for (QueryColumn queryColumn : keyWordLikeFields) {
                    String fieldName = queryColumn.getName();
                    String paramName = StrUtil.toCamelCase(fieldName);
                    String paramValue = requestParams.getStr(paramName);
                    if (ObjectUtil.isNotEmpty(paramValue)) {
                        queryWrapper.and(queryColumn.like(paramValue));
                    }
                }
            } else {
                // keyWord值非空，使用keyWord构建
                // 初始化一个空的 QueryCondition
                QueryCondition orCondition = null;
                for (QueryColumn queryColumn : keyWordLikeFields) {
                    QueryCondition condition = queryColumn.like(keyWord);
                    if (orCondition == null) {
                        orCondition = condition;
                    } else {
                        orCondition = orCondition.or(condition);
                    }
                }
                queryWrapper.and(orCondition);
            }
        }
        if (ObjectUtil.isNotEmpty(select)) {
            queryWrapper.select(select);
        }
        // 排序
        order(queryWrapper, entityClass);
        return queryWrapper;
    }

    private void order(QueryWrapper queryWrapper, Class<T> entityClass) {
        Table tableAnnotation = AnnotationUtil.getAnnotation(entityClass, Table.class);
        if (ObjectUtil.isEmpty(tableAnnotation)) {
            // 该对象没有@Table注解，非Entity对象
            return;
        }
        String tableAlias = "";
        List<QueryTable> queryTables = (List<QueryTable>) ReflectUtil.getFieldValue(queryWrapper, "queryTables");
        if (ObjectUtil.isNotEmpty(queryTables)) {
            // 取主表作为排序字段别名
            QueryTable queryTable = queryTables.get(0);
            tableAlias = queryTable.getName() + ".";
        }
        String order = requestParams.getStr("order",
                tableAnnotation.camelToUnderline() ? "create_time" : "createTime");
        String sort = requestParams.getStr("sort", "desc");
        if (StrUtil.isNotEmpty(order) && StrUtil.isNotEmpty(sort)) {
            queryWrapper.orderBy(
                    tableAlias + (tableAnnotation.camelToUnderline() ? StrUtil.toUnderlineCase(order) : order),
                    sort.equals("asc"));
        }
    }
}
