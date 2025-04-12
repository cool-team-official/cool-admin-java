package com.cool.modules.dict.service.impl;

import static com.cool.modules.dict.entity.table.DictInfoEntityTableDef.DICT_INFO_ENTITY;
import static com.cool.modules.dict.entity.table.DictTypeEntityTableDef.DICT_TYPE_ENTITY;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.cool.core.base.BaseServiceImpl;
import com.cool.core.base.ModifyEnum;
import com.cool.core.i18n.I18nGenerator;
import com.cool.modules.dict.entity.DictInfoEntity;
import com.cool.modules.dict.entity.DictTypeEntity;
import com.cool.modules.dict.mapper.DictInfoMapper;
import com.cool.modules.dict.mapper.DictTypeMapper;
import com.cool.modules.dict.service.DictInfoService;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 字典信息
 */
@Service
@RequiredArgsConstructor
public class DictInfoServiceImpl extends BaseServiceImpl<DictInfoMapper, DictInfoEntity> implements
    DictInfoService {

    final private DictTypeMapper dictTypeMapper;

    @Override
    public Object data(List<String> types) {
        Dict result = Dict.create();
        QueryWrapper find = QueryWrapper.create();
        find.select(DICT_TYPE_ENTITY.ID, DICT_TYPE_ENTITY.KEY,
            DICT_TYPE_ENTITY.NAME);
        if (CollectionUtil.isNotEmpty(types)) {
            find.and(DICT_TYPE_ENTITY.KEY.in(types));
        }
        List<DictTypeEntity> typeData = dictTypeMapper.selectListByQuery(find);
        if (typeData.isEmpty()) {
            return result;
        }
        List<DictInfoEntity> infos = this.list(QueryWrapper.create()
            .select(DictInfoEntity::getId, DictInfoEntity::getName, DictInfoEntity::getTypeId,
                DictInfoEntity::getParentId, DictInfoEntity::getValue)
            .in(DictInfoEntity::getTypeId,
                typeData.stream().map(DictTypeEntity::getId).collect(Collectors.toList()))
            .orderBy(DICT_INFO_ENTITY.ORDER_NUM.getName(), DICT_INFO_ENTITY.CREATE_TIME.getName()));
        typeData.forEach(item -> {
            List<Dict> datas = new ArrayList<>();
            infos.stream().filter(d -> d.getTypeId().equals(item.getId())).toList().forEach(d -> {
                Dict data = Dict.create();
                data.set("typeId", d.getTypeId());
                data.set("parentId", d.getParentId());
                data.set("name", d.getName());
                data.set("id", d.getId());
                data.set("value", StrUtil.isEmpty(d.getValue()) ? null : d.getValue());
                try {
                    data.set("value", Integer.parseInt(d.getValue()));
                } catch (Exception ignored) {
                }
                datas.add(data);
            });
            result.set(item.getKey(), datas);
        });
        return result;
    }

    @Override
    public Object types() {
        List<DictInfoEntity> infos = this.list();
        if (ObjUtil.isEmpty(infos)) {
            return infos;
        }
        List<Dict> datas = new ArrayList<>();
        infos.stream().forEach(d -> {
            Dict data = Dict.create();
            data.set("typeId", d.getTypeId());
            data.set("parentId", d.getParentId());
            data.set("name", d.getName());
            data.set("id", d.getId());
            data.set("value", StrUtil.isEmpty(d.getValue()) ? null : d.getValue());
            try {
                data.set("value", Integer.parseInt(d.getValue()));
            } catch (Exception ignored) {
            }
            datas.add(data);
        });
        return datas;
    }

    @Override
    public boolean delete(Long... ids) {
        super.delete(ids);
        for (Long id : ids) {
            this.delDictChild(id);
        }
        return true;
    }

    /**
     * 删除子菜单
     *
     * @param id 删除的菜单ID
     */
    private void delDictChild(Long id) {
        List<DictInfoEntity> delDict = list(
            QueryWrapper.create().eq(DictInfoEntity::getParentId, id));
        if (CollectionUtil.isEmpty(delDict)) {
            return;
        }
        Long[] ids = delDict.stream().map(DictInfoEntity::getId).toArray(Long[]::new);
        if (ArrayUtil.isNotEmpty(ids)) {
            delete(ids);
            for (Long delId : ids) {
                this.delDictChild(delId);
            }
        }
    }
    @Override
    public void modifyAfter(JSONObject requestParams, DictInfoEntity entity, ModifyEnum type) {
        if (ModifyEnum.ADD.equals(type) || ModifyEnum.UPDATE.equals(type)) {
            SpringUtil.getBean(I18nGenerator.class).asyncGenBaseDictInfo();
        }
    }
}