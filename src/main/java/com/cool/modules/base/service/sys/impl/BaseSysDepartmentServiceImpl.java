package com.cool.modules.base.service.sys.impl;

import cn.hutool.json.JSONObject;
import com.cool.core.base.BaseServiceImpl;
import com.cool.core.util.CoolSecurityUtil;
import com.cool.modules.base.entity.sys.BaseSysDepartmentEntity;
import com.cool.modules.base.entity.sys.BaseSysUserEntity;
import com.cool.modules.base.mapper.sys.BaseSysDepartmentMapper;
import com.cool.modules.base.mapper.sys.BaseSysUserMapper;
import com.cool.modules.base.service.sys.BaseSysDepartmentService;
import com.cool.modules.base.service.sys.BaseSysPermsService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统部门
 */
@RequiredArgsConstructor
@Service
public class BaseSysDepartmentServiceImpl extends
    BaseServiceImpl<BaseSysDepartmentMapper, BaseSysDepartmentEntity>
    implements BaseSysDepartmentService {

    final private BaseSysUserMapper baseSysUserMapper;

    final private BaseSysPermsService baseSysPermsService;

    @Override
    public void order(List<BaseSysDepartmentEntity> list) {
        list.forEach(baseSysDepartmentEntity -> {
            UpdateChain.of(BaseSysDepartmentEntity.class)
                .set(BaseSysDepartmentEntity::getOrderNum, baseSysDepartmentEntity.getOrderNum())
                .set(BaseSysDepartmentEntity::getParentId, baseSysDepartmentEntity.getParentId())
                .eq(BaseSysDepartmentEntity::getId, baseSysDepartmentEntity.getId()).update();
        });
    }

    @Override
    public List<BaseSysDepartmentEntity> list(JSONObject requestParams, QueryWrapper queryWrapper) {
        String username = CoolSecurityUtil.getAdminUsername();
        Long[] loginDepartmentIds = baseSysPermsService.loginDepartmentIds();
        if (loginDepartmentIds != null && loginDepartmentIds.length == 0) {
            return new ArrayList<>();
        }
        List<BaseSysDepartmentEntity> list = this.list(
            QueryWrapper.create()
                .in(BaseSysDepartmentEntity::getId, loginDepartmentIds, !username.equals("admin"))
                .orderBy(BaseSysDepartmentEntity::getOrderNum, false));
        list.forEach(e -> {
            List<BaseSysDepartmentEntity> parentDepartment = list.stream()
                .filter(sysDepartmentEntity -> e.getParentId() != null
                    && e.getParentId().equals(sysDepartmentEntity.getId()))
                .toList();
            if (!parentDepartment.isEmpty()) {
                e.setParentName(parentDepartment.get(0).getName());
            }
        });
        return list;
    }

    @Override
    public boolean delete(JSONObject requestParams, Long... ids) {
        super.delete(ids);
        // 是否删除对应用户 否则移动到顶层部门
        if (requestParams.getBool("deleteUser")) {
            return baseSysUserMapper
                .deleteByQuery(
                    QueryWrapper.create().in(BaseSysUserEntity::getDepartmentId, (Object) ids)) > 0;
        } else {
            BaseSysDepartmentEntity topDepartment = getOne(
                QueryWrapper.create().isNull(BaseSysDepartmentEntity::getParentId));
            if (topDepartment != null) {
                UpdateChain.of(BaseSysUserEntity.class)
                    .set(BaseSysUserEntity::getDepartmentId, topDepartment.getId())
                    .in(BaseSysUserEntity::getDepartmentId, (Object) ids).update();
            }
        }
        return false;
    }
}