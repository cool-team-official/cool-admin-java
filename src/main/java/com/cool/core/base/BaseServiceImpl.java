package com.cool.core.base;

import cn.hutool.json.JSONObject;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 基础service实现类
 *
 * @param <M> Mapper 类
 * @param <T> 实体
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity<T>> extends
    ServiceImpl<M, T>
    implements BaseService<T> {

    @Override
    public Long add(T entity) {
        mapper.insertSelective(entity);
        return entity.getId();
    }

    @Override
    public Object add(JSONObject requestParams, T entity) {
        this.modifyBefore(requestParams, entity, ModifyEnum.ADD);
        this.add(entity);
        this.modifyAfter(requestParams, entity, ModifyEnum.ADD);
        return entity.getId();
    }

    @Override
    public Object addBatch(JSONObject requestParams, List<T> entitys) {
        this.modifyBefore(requestParams, null, ModifyEnum.ADD);
        List<Long> ids = new ArrayList<>();
        entitys.forEach(e -> ids.add(this.add(e)));
        requestParams.set("ids", ids);
        this.modifyAfter(requestParams, null, ModifyEnum.ADD);
        return ids;
    }

    @Override
    public boolean delete(Long... ids) {
        return mapper.deleteBatchByIds(Arrays.asList(ids)) > 0;
    }

    @Override
    public boolean delete(JSONObject requestParams, Long... ids) {
        this.modifyBefore(requestParams, null, ModifyEnum.DELETE);
        boolean flag = this.delete(ids);
        if (flag) {
            this.modifyAfter(requestParams, null, ModifyEnum.DELETE);
        }
        return flag;
    }

    @Override
    public boolean update(T entity) {
        return mapper.update(entity) > 0;
    }

    @Override
    public boolean update(JSONObject requestParams, T entity) {
        this.modifyBefore(requestParams, entity, ModifyEnum.UPDATE);
        boolean flag = this.update(entity);
        if (flag) {
            this.modifyAfter(requestParams, entity, ModifyEnum.UPDATE);
        }
        return flag;
    }

    @Override
    public Object list(JSONObject requestParams, QueryWrapper queryWrapper) {
        return this.list(queryWrapper);
    }

    @Override
    public <R> List<R> list(JSONObject requestParams, QueryWrapper queryWrapper, Class<R> asType) {
        return mapper.selectListByQueryAs(queryWrapper, asType);
    }

    @Override
    public Object listWithRelations(JSONObject requestParams, QueryWrapper queryWrapper) {
        return mapper.selectListWithRelationsByQuery(queryWrapper);
    }

    @Override
    public Object page(JSONObject requestParams, Page<T> page, QueryWrapper queryWrapper) {
        return this.page(page, queryWrapper);
    }

    @Override
    public <R> Page<R> page(JSONObject requestParams, Page page, QueryWrapper queryWrapper,
        Class<R> asType) {
        return mapper.paginateAs(page, queryWrapper, asType);
    }

    @Override
    public Object pageWithRelations(JSONObject requestParams, Page<T> page,
        QueryWrapper queryWrapper) {
        return mapper.paginateWithRelations(page, queryWrapper);
    }

    @Override
    public Object info(JSONObject requestParams, Long id) {
        return info(id);
    }

    @Override
    public Object info(Long id) {
        return mapper.selectOneById(id);
    }

    @Override
    public void modifyAfter(JSONObject requestParams, T t) {

    }

    @Override
    public void modifyAfter(JSONObject requestParams, T t, ModifyEnum type) {
        modifyAfter(requestParams, t);
    }

    @Override
    public void modifyBefore(JSONObject requestParams, T t) {

    }

    @Override
    public void modifyBefore(JSONObject requestParams, T t, ModifyEnum type) {

    }
}
