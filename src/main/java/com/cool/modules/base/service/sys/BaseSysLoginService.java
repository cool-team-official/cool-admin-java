package com.cool.modules.base.service.sys;

import com.cool.core.enums.UserTypeEnum;
import com.cool.modules.base.dto.sys.BaseSysLoginDto;

/**
 * 系统登录
 */
public interface BaseSysLoginService {
    /**
     * 验证码
     *
     * @param type   类型 svg base64 svg是node版本的， java版本用base64， svg未实现
     * @param width  宽度
     * @param height 高度
     * @return base64 验证码与ID
     */
    Object captcha(UserTypeEnum userTypeEnum, String type, Integer width, Integer height);

    /**
     * 校验验证码
     */
    void captchaCheck(String captchaId, String code);

    /**
     * 登录
     *
     * @param baseSysLoginDto 登录必要信息
     * @return token与相关的过期信息
     */
    Object login(BaseSysLoginDto baseSysLoginDto);

    /**
     * 退出登录
     *
     * @param adminUserId 用户ID
     * @param username    用户名称
     */
    void logout(Long adminUserId, String username);

    /**
     * 刷新token
     *
     * @param refreshToken 刷新token
     * @return 新的token
     */
    Object refreshToken(String refreshToken);
}
