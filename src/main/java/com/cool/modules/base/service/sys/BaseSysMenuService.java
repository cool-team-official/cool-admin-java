package com.cool.modules.base.service.sys;

import com.cool.core.base.BaseService;
import com.cool.modules.base.entity.sys.BaseSysMenuEntity;
import java.util.List;
import java.util.Map;

/**
 * 系统菜单
 */
public interface BaseSysMenuService extends BaseService<BaseSysMenuEntity> {

    Object export(List<Long> ids);

    boolean importMenu(List<BaseSysMenuEntity> menus);

    void create(Map<String, Object> params);
}
