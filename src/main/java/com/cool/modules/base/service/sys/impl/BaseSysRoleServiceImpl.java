package com.cool.modules.base.service.sys.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONObject;
import com.cool.core.base.BaseServiceImpl;
import com.cool.core.base.ModifyEnum;
import com.cool.core.exception.CoolException;
import com.cool.core.util.CoolSecurityUtil;
import com.cool.modules.base.entity.sys.BaseSysRoleDepartmentEntity;
import com.cool.modules.base.entity.sys.BaseSysRoleEntity;
import com.cool.modules.base.entity.sys.BaseSysRoleMenuEntity;
import com.cool.modules.base.mapper.sys.BaseSysRoleDepartmentMapper;
import com.cool.modules.base.mapper.sys.BaseSysRoleMapper;
import com.cool.modules.base.mapper.sys.BaseSysRoleMenuMapper;
import com.cool.modules.base.service.sys.BaseSysPermsService;
import com.cool.modules.base.service.sys.BaseSysRoleService;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统角色
 */
@RequiredArgsConstructor
@Service
public class BaseSysRoleServiceImpl extends BaseServiceImpl<BaseSysRoleMapper, BaseSysRoleEntity>
        implements BaseSysRoleService {

    final private BaseSysRoleMapper baseSysRoleMapper;

    final private BaseSysRoleMenuMapper baseSysRoleMenuMapper;

    final private BaseSysRoleDepartmentMapper baseSysRoleDepartmentMapper;

    final private BaseSysPermsService baseSysPermsService;

    @Override
    public Object add(JSONObject requestParams, BaseSysRoleEntity entity) {
        BaseSysRoleEntity checkLabel = getOne(QueryWrapper.create().eq(BaseSysRoleEntity::getLabel, entity.getLabel()));
        if (checkLabel != null) {
            throw new CoolException("标识已存在");
        }
        entity.setUserId((CoolSecurityUtil.getAdminUserInfo(requestParams).getLong("userId")));
        return super.add(requestParams, entity);
    }

    @Override
    public BaseSysRoleEntity info(Long id) {
        BaseSysRoleEntity roleEntity = getById(id);
        Long[] menuIdList = new Long[0];
        Long[] departmentIdList = new Long[0];
        if (roleEntity != null) {
            List<BaseSysRoleMenuEntity> list = baseSysRoleMenuMapper
                    .selectListByQuery(QueryWrapper.create().eq(BaseSysRoleMenuEntity::getRoleId, id, !id.equals(1L)));
            menuIdList = list.stream().map(BaseSysRoleMenuEntity::getMenuId).toArray(Long[]::new);

            List<BaseSysRoleDepartmentEntity> departmentEntities = baseSysRoleDepartmentMapper.selectListByQuery(
                    QueryWrapper.create().eq(BaseSysRoleDepartmentEntity::getRoleId, id, !id.equals(1L)));

            departmentIdList = departmentEntities.stream().map(BaseSysRoleDepartmentEntity::getDepartmentId)
                    .toArray(Long[]::new);
            
            
            roleEntity.setMenuIdList(Arrays.asList( menuIdList ));
            roleEntity.setDepartmentIdList(Arrays.asList(departmentIdList ));
            
        }
        
        return roleEntity;
    }

    @Override
    public void modifyAfter(JSONObject requestParams, BaseSysRoleEntity baseSysRoleEntity, ModifyEnum type) {
        if (type == ModifyEnum.DELETE) {
            Long[] ids = requestParams.get("ids", Long[].class);
            for (Long id : ids) {
                baseSysPermsService.refreshPermsByRoleId(id);
            }
        } else {
            baseSysPermsService.updatePerms(baseSysRoleEntity.getId(), requestParams.get("menuIdList", Long[].class),
                    requestParams.get("departmentIdList", Long[].class));
        }
    }

    @Override
    public Object list(JSONObject requestParams, QueryWrapper queryWrapper) {
        return baseSysRoleMapper.selectListByQuery(queryWrapper.ne(BaseSysRoleEntity::getId, 1L).and(qw -> {
            JSONObject object = CoolSecurityUtil.getAdminUserInfo(requestParams);
            qw.eq(BaseSysRoleEntity::getUserId, object.get("userId")).or(w -> {
                w.in(BaseSysRoleEntity::getId,
                        (Object) object.get("roleIds", Long[].class));
            });
        }, !CoolSecurityUtil.getAdminUsername().equals("admin")));
    }
}