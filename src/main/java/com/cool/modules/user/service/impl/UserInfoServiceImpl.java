package com.cool.modules.user.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.user.entity.UserInfoEntity;
import com.cool.modules.user.mapper.UserInfoMapper;
import com.cool.modules.user.service.UserInfoService;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends BaseServiceImpl<UserInfoMapper, UserInfoEntity> implements
    UserInfoService {

    @Override
    public UserInfoEntity person(Long userId) {
        UserInfoEntity info = mapper.selectOneById(userId);
        info.setPassword(null);
        return info;
    }
}
