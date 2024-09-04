package com.cool.modules.user.service;

import com.cool.core.base.BaseService;
import com.cool.modules.user.entity.UserWxEntity;

/**
 * 微信用户
 */
public interface UserWxService extends BaseService<UserWxEntity> {

    /**
     * 获取小程序用户信息
     */
    UserWxEntity getMiniUserInfo(String code, String encryptedData, String iv);
}
