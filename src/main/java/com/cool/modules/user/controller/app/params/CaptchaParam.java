package com.cool.modules.user.controller.app.params;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CaptchaParam {
    private String type;
    private Integer width = 150;
    private Integer height = 50;
}
