package com.cool.modules.user.controller.app.params;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginParam {

    /*******小程序/公众号/微信APP授权 登录*******/
    private String code;

    private String encryptedData;

    private String iv;


    /*******手机号登录*******/
    private String phone;

    private String smsCode;


    /*******一键手机号登录*******/
    private String access_token;

    private String openid;

    private String appId;


    /*******密码登录*******/
    private String password;
}
