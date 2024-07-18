package com.cool.core.request;


import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.Arrays;
import java.util.List;
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

    private Environment evn;

    public CrudOption(JSONObject requestParams) {
        this.requestParams = requestParams;
        this.queryWrapper = QueryWrapper.create();
        this.evn = SpringUtil.getBean(Environment.class);
    }

    public CrudOption<T> fieldEq(QueryColumn... fields) {
        this.fieldEq = fields;
        return this;
    }

    public QueryWrapper getQueryWrapper(Class<T> entityClass) {
        return build(this.queryWrapper, entityClass);
    }

    public CrudOption<T> queryWrapper(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
        return this;
    }

    public CrudOption<T> keyWordLikeFields(QueryColumn... fields) {
        this.keyWordLikeFields = fields;
        return this;
    }

    public CrudOption<T> select(QueryColumn... selects) {
        this.select = selects;
        return this;
    }


    /**
     * 构建查询条件
     *
     * @return QueryWrapper
     */
    private QueryWrapper build(QueryWrapper queryWrapper, Class<T> entityClass) {
        if (ObjectUtil.isNotEmpty(fieldEq)) {
            Arrays.stream(fieldEq).toList().forEach(filed -> queryWrapper.and(
                filed.eq(requestParams.get(StrUtil.toCamelCase(filed.getName())))));
        }
        Object keyWord = requestParams.get("keyWord");
        if (ObjectUtil.isNotEmpty(this.keyWordLikeFields) && ObjectUtil.isNotEmpty(keyWord)) {
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
            tableAlias = "`" + queryTable.getName() + "`.";
        }
        String order = requestParams.getStr("order",
            tableAnnotation.camelToUnderline() ? "create_time" : "createTime");
        String sort = requestParams.getStr("sort", "desc");
        if (StrUtil.isNotEmpty(order) && StrUtil.isNotEmpty(sort)) {
            queryWrapper.orderBy(
                tableAlias + "`" + (tableAnnotation.camelToUnderline() ? StrUtil.toUnderlineCase(order) : order) + "`",
                sort.equals("asc"));
        }
    }
}
