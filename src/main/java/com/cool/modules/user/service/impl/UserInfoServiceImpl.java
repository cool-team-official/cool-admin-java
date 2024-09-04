package com.cool.modules.user.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.user.entity.UserInfoEntity;
import com.cool.modules.user.mapper.UserInfoMapper;
import com.cool.modules.user.service.UserInfoService;
import com.cool.modules.user.util.UserSmsUtil;
import com.cool.modules.user.util.UserSmsUtil.SendSceneEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl extends BaseServiceImpl<UserInfoMapper, UserInfoEntity> implements
    UserInfoService {

    private final UserSmsUtil userSmsUtil;

    @Override
    public UserInfoEntity person(Long userId) {
        UserInfoEntity info = getById(userId);
        info.setPassword(null);
        return info;
    }

    @Override
    public void updatePassword(Long userId, String password, String code) {
        UserInfoEntity info = getById(userId);
        userSmsUtil.checkVerifyCode(info.getPhone(), code, SendSceneEnum.ALL);
        info.setPassword(MD5.create().digestHex(password));
        info.updateById();
    }

    @Override
    public void logoff(Long userId) {
        UserInfoEntity info = new UserInfoEntity();
        info.setId(userId);
        info.setStatus(2);
        info.setNickName("已注销-00" + userId);
        info.updateById();
    }

    @Override
    public void bindPhone(Long userId, String phone, String code) {
        userSmsUtil.checkVerifyCode(phone, code, SendSceneEnum.ALL);
        UserInfoEntity info = new UserInfoEntity();
        info.setId(userId);
        info.setPhone(phone);
        info.updateById();
    }
}
