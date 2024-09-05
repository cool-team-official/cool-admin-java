package com.cool.modules.user.service.impl;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.cool.core.base.BaseServiceImpl;
import com.cool.core.exception.CoolPreconditions;
import com.cool.modules.user.entity.UserWxEntity;
import com.cool.modules.user.mapper.UserWxMapper;
import com.cool.modules.user.proxy.WxProxy;
import com.cool.modules.user.service.UserWxService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;

/**
 * 微信用户
 */
@Service
@RequiredArgsConstructor
public class UserWxServiceImpl extends BaseServiceImpl<UserWxMapper, UserWxEntity> implements UserWxService {

    private final WxProxy wxProxy;

    /**
     * 获得小程序用户信息
     */
    public UserWxEntity getMiniUserInfo(String code, String encryptedData, String iv) {
        // 获取 session
        WxMaJscode2SessionResult result = null;
        try {
            result = wxProxy.getSessionInfo(code);
            // 解密数据
            WxMaUserInfo wxMaUserInfo = wxProxy.getUserInfo(result.getSessionKey(), encryptedData, iv);
            if (ObjUtil.isNotEmpty(wxMaUserInfo)) {
                UserWxEntity userWxEntity = BeanUtil.copyProperties(wxMaUserInfo, UserWxEntity.class);
                userWxEntity.setOpenid(result.getOpenid());
                userWxEntity.setUnionid(wxMaUserInfo.getUnionId());
                return getBySave(userWxEntity, 0);
            }
        } catch (WxErrorException e) {
            CoolPreconditions.alwaysThrow(e.getMessage(), e);
        }
        CoolPreconditions.alwaysThrow("获得小程序用户信息");
        return null;
    }

    public UserWxEntity getBySave(UserWxEntity entity, int type) {
        UserWxEntity one = this.getOne(
            QueryWrapper.create().eq(UserWxEntity::getOpenid, entity.getOpenid()));
        if (ObjUtil.isEmpty(one)) {
            entity.setType(type);
            super.save(entity);
            return entity;
        }
        return one;
    }
}