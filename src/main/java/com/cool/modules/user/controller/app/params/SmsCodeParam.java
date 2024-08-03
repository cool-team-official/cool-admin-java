package com.cool.modules.user.controller.app.params;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SmsCodeParam {
    private String phone;
    private String captchaId;
    private String code;
}
