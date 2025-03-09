package com.cool.modules.user.entity;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Index;

/**
 * 用户模块-收货地址
 */
@Getter
@Setter
@Table(value = "user_address", comment = "用户模块-收货地址")
public class UserAddressEntity extends BaseEntity<UserAddressEntity> {

    @Index
    @ColumnDefine(comment = "用户ID", notNull = true)
    private Long userId;

    @ColumnDefine(comment = "联系人", notNull = true)
    private String contact;

    @Index
    @ColumnDefine(comment = "手机号", length = 11, notNull = true)
    private String phone;

    @ColumnDefine(comment = "省", notNull = true)
    private String province;

    @ColumnDefine(comment = "市", notNull = true)
    private String city;

    @ColumnDefine(comment = "区", notNull = true)
    private String district;

    @ColumnDefine(comment = "地址", notNull = true)
    private String address;

    @ColumnDefine(comment = "是否默认", defaultValue = "false")
    private Boolean isDefault;
}
