package com.cool.modules.user.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.jwt.JWT;
import com.cool.core.cache.CoolCache;
import com.cool.core.exception.CoolException;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.security.jwt.JwtTokenUtil;
import com.cool.modules.base.security.CoolSecurityUtil;
import com.cool.modules.user.entity.UserInfoEntity;
import com.cool.modules.user.mapper.UserInfoMapper;
import com.cool.modules.user.service.UserLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserLoginServiceImpl implements UserLoginService {

    private final CoolCache coolCache;

    private final AuthenticationManager authenticationManager;

    private final CoolSecurityUtil coolSecurityUtil;

    private final JwtTokenUtil jwtTokenUtil;

    private final UserInfoMapper userInfoMapper;

    @Override
    public void smsCode(String phone, String captchaId, String code) {

    }

    @Override
    public Object phoneVerifyCode(String phone, String smsCode) {
        return null;
    }

    @Override
    public Object refreshToken(String refreshToken) {
        JWT jwt = jwtTokenUtil.getTokenInfo(refreshToken);
        try {
            CoolPreconditions.check(jwt == null || !(Boolean) jwt.getPayload("isRefresh"),
                    "错误的token");

            UserInfoEntity userInfoEntity =
                    userInfoMapper.selectOneById(Convert.toLong(jwt.getPayload("userId")));
            Dict tokenInfo =
                    Dict.create()
                            .set("userId", userInfoEntity.getId());
            String token = jwtTokenUtil.generateToken(tokenInfo);
            refreshToken = jwtTokenUtil.generateRefreshToken(tokenInfo);
            return Dict.create()
                    .set("token", token)
                    .set("expire", jwtTokenUtil.getExpire())
                    .set("refreshToken", refreshToken)
                    .set("refreshExpire", jwtTokenUtil.getRefreshExpire());
        } catch (Exception e) {
            throw new CoolException("错误的token", e);
        }
    }
}
