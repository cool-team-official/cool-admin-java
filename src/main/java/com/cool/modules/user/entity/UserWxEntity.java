package com.cool.modules.user.entity;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import com.tangzc.mybatisflex.autotable.annotation.UniIndex;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Index;

@Getter
@Setter
@Table(value = "user_wx", comment = "微信用户")
public class UserWxEntity extends BaseEntity<UserWxEntity> {

    @Index
    @ColumnDefine(comment = "微信unionid")
    private String unionid;

    @UniIndex
    @ColumnDefine(comment = "微信openid", notNull = true)
    private String openid;

    @ColumnDefine(comment = "头像")
    private String avatarUrl;

    @ColumnDefine(comment = "昵称")
    private String nickName;

    @ColumnDefine(comment = "性别 0-未知 1-男 2-女", defaultValue = "0")
    private Integer gender;

    @ColumnDefine(comment = "语言")
    private String language;

    @ColumnDefine(comment = "城市")
    private String city;

    @ColumnDefine(comment = "省份")
    private String province;

    @ColumnDefine(comment = "国家")
    private String country;

    @ColumnDefine(comment = "类型 0-小程序 1-公众号 2-H5 3-APP", defaultValue = "0")
    private Integer type;
}
