package com.cool.modules.user.entity;

import com.cool.core.base.BaseEntity;
import com.tangzc.autotable.annotation.Index;
import com.tangzc.autotable.annotation.enums.IndexTypeEnum;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import com.mybatisflex.annotation.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "user_info", comment = "用户信息")
public class UserInfoEntity extends BaseEntity<UserInfoEntity> {

    @Index(type = IndexTypeEnum.UNIQUE)
    @ColumnDefine(comment = "登录唯一ID", notNull = true)
    private String unionid;

    @ColumnDefine(comment = "头像", notNull = true)
    private String avatarUrl;

    @ColumnDefine(comment = "昵称", notNull = true)
    private String nickName;

    @Index
    @ColumnDefine(comment = "手机号", notNull = true)
    private String phone;

    @ColumnDefine(comment = "性别 0-未知 1-男 2-女", defaultValue = "0")
    private String gender;

    @ColumnDefine(comment = "状态 0-禁用 1-正常 2-已注销", defaultValue = "1")
    private String status;

    @ColumnDefine(comment = "登录方式 0-小程序 1-公众号 2-H5", defaultValue = "0")
    private String loginType;

    @ColumnDefine(comment = "密码", notNull = true)
    private String password;
}
