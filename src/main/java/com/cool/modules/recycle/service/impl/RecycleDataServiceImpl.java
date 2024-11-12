package com.cool.modules.recycle.service.impl;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cool.core.base.BaseServiceImpl;
import com.cool.core.base.service.MapperProviderService;
import com.cool.modules.base.entity.sys.BaseSysUserEntity;
import com.cool.modules.base.service.sys.BaseSysUserService;
import com.cool.modules.recycle.entity.RecycleDataEntity;
import com.cool.modules.recycle.mapper.RecycleDataMapper;
import com.cool.modules.recycle.service.RecycleDataService;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 数据回收站
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecycleDataServiceImpl extends BaseServiceImpl<RecycleDataMapper, RecycleDataEntity>
    implements RecycleDataService {

    final private BaseSysUserService baseSysUserService;

    final private MapperProviderService mapperProviderService;

    @Override
    public Object page(JSONObject requestParams, Page<RecycleDataEntity> page,
        QueryWrapper queryWrapper) {
        String keyWord = requestParams.getStr("keyWord");
        if (ObjUtil.isNotEmpty(keyWord)) {
            List<Long> list = baseSysUserService
                .list(QueryWrapper.create().select(BaseSysUserEntity::getId)
                    .like(BaseSysUserEntity::getName, keyWord))
                .stream().map(BaseSysUserEntity::getId).toList();
            queryWrapper.like(RecycleDataEntity::getUrl, keyWord).or(w -> {
                w.in(RecycleDataEntity::getUserId, list, ObjUtil.isNotEmpty(list));
            });
        }
        Page<RecycleDataEntity> iPage = page(page, queryWrapper);
        List<RecycleDataEntity> records = iPage.getRecords();
        List<Long> list = records.stream().map(RecycleDataEntity::getUserId)
            .filter(ObjUtil::isNotEmpty).toList();

        if (ObjUtil.isNotEmpty(list)) {
            Map<Long, String> map = baseSysUserService
                .list(QueryWrapper.create()
                    .select(BaseSysUserEntity::getId, BaseSysUserEntity::getName)
                    .in(BaseSysUserEntity::getId, list))
                .stream()
                .collect(Collectors.toMap(BaseSysUserEntity::getId, BaseSysUserEntity::getName));
            records.forEach(o -> {
                if (map.containsKey(o.getUserId())) {
                    o.setUserName(map.get(o.getUserId()));
                }
            });
        }
        return iPage;
    }

    @Override
    public Boolean restore(List<Long> ids) {
        if (ObjUtil.isEmpty(ids)) {
            return false;
        }
        List<RecycleDataEntity> list = list(
            QueryWrapper.create().in(RecycleDataEntity::getId, ids));
        list.forEach(o -> {
            // 处理恢复数据
            boolean flag = handlerRestore(o);
            if (flag) {
                // 删除回收站记录
                o.removeById();
            }
        });
        return true;
    }

    /**
     * 处理数据恢复
     */
    private boolean handlerRestore(RecycleDataEntity recycleDataEntity) {
        RecycleDataEntity.EntityInfo entityInfo = recycleDataEntity.getEntityInfo();
        try {
            Class<?> entityClass = ClassUtil.loadClass(entityInfo.getEntityClassName());
            List<Object> records = recycleDataEntity.getData();
            BaseMapper<?> baseMapper = mapperProviderService.getMapperByEntityClass(
                entityClass);
            // 插入数据
            List insertList = new ArrayList<>();
            for (Object record : records) {
                Object entity = JSONUtil.toBean(JSONUtil.parseObj(record), entityClass);
                Method getIdMethod = entityClass.getMethod("getId");
                Object id = getIdMethod.invoke(entity);
                if (baseMapper.selectOneById((Long) id) == null) {
                    insertList.add(entity);
                }
            }
            baseMapper.insertBatch(insertList);
            return true;
        } catch (Exception e) {
            log.error("恢复数据失败", e);
        }
        return false;
    }
}