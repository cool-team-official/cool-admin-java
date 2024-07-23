package com.cool.modules.user.service;

/**
 * 用户登录
 */
public interface UserLoginService {

    /**
     * 发送短信验证码
     * @param phone
     * @param captchaId
     * @param code
     */
   void smsCode(String phone, String captchaId, String code);

    /**
     * 手机号验证码登录
     * @param phone
     * @param smsCode
     */
    Object phoneVerifyCode(String phone, String smsCode);


    /**
     * 刷新token
     *
     * @param refreshToken 刷新token
     * @return 新的token
     */
    Object refreshToken(String refreshToken);
}
