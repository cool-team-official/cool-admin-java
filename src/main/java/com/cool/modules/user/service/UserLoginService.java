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
    /**
     * 小程序登录
     */
    Object mini(String code, String encryptedData, String iv);
    /**
     * 公众号登录
     */
    Object mp(String code);
    /**
     * 微信APP授权登录
     */
    Object wxApp(String code);

    /**
     * 一键手机号登录
     */
    Object uniPhone(String accessToken, String openid, String appId);
    /**
     * 绑定小程序手机号
     */
    Object miniPhone(String code, String encryptedData, String iv);

    /**
     * 密码登录
     */
    Object password(String phone, String password);
}
