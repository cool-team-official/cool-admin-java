package com.cool.modules.user.controller.app.params;

import lombok.Getter;
import lombok.Setter;

/**
 * 刷新token
 */
@Setter
@Getter
public class RefreshTokenParam {
    private String refreshToken;
}
