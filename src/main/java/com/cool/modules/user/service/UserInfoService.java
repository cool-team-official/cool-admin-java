package com.cool.modules.user.service;

import com.cool.core.base.BaseService;
import com.cool.modules.user.entity.UserInfoEntity;

public interface UserInfoService extends BaseService<UserInfoEntity> {
    /**
     * 用户个人信息
     * @param userId
     * @return
     */
    UserInfoEntity person(Long userId);
}
