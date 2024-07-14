package com.cool.core.request;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.Arrays;
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
        this.queryWrapper = new QueryWrapper();
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
            QueryCondition queryCondition = new QueryCondition();
            Arrays.stream(keyWordLikeFields).toList().forEach(likeQueryColumn -> {
                if (ObjectUtil.isEmpty(queryCondition.getColumn())) {
                    queryCondition.setColumn(likeQueryColumn);
                    queryCondition.setLogic(" LIKE ");
                    queryCondition.setValue("%" + keyWord + "%");
                } else {
                    queryCondition.or(likeQueryColumn.like(keyWord));
                }
            });
            queryWrapper.and(queryCondition);
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
        String order = requestParams.getStr("order",
            tableAnnotation.camelToUnderline() ? "create_time" : "createTime");
        String sort = requestParams.getStr("sort", "desc");
        if (StrUtil.isNotEmpty(order) && StrUtil.isNotEmpty(sort)) {
            queryWrapper.orderBy(
                tableAnnotation.camelToUnderline() ? StrUtil.toUnderlineCase(order) : order,
                sort.equals("asc"));
        }
    }
}
