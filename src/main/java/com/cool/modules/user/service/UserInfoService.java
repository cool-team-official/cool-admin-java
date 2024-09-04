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

    /**
     * 更新用户密码
     */
    void updatePassword(Long userId, String password, String code);

    /**
     * 注销
     */
    void logoff(Long currentUserId);

    /**
     * 绑定手机号
     */
    void bindPhone(Long currentUserId, String phone, String code);
}
