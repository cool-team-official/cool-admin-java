package com.cool.modules.user.service;

import com.cool.core.base.BaseService;
import com.cool.modules.user.entity.UserAddressEntity;

/**
 * 用户模块-收货地址
 */
public interface UserAddressService extends BaseService<UserAddressEntity> {

    /**
     * 获取默认地址
     */
    Object getDefault(Long userId);
}
