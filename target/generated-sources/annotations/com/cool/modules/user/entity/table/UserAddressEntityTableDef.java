package com.cool.modules.user.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class UserAddressEntityTableDef extends TableDef {

    /**
     * 用户模块-收货地址
     */
    public static final UserAddressEntityTableDef USER_ADDRESS_ENTITY = new UserAddressEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn CITY = new QueryColumn(this, "city");

    public final QueryColumn PHONE = new QueryColumn(this, "phone");

    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    public final QueryColumn ADDRESS = new QueryColumn(this, "address");

    public final QueryColumn CONTACT = new QueryColumn(this, "contact");

    public final QueryColumn DISTRICT = new QueryColumn(this, "district");

    public final QueryColumn PROVINCE = new QueryColumn(this, "province");

    public final QueryColumn IS_DEFAULT = new QueryColumn(this, "is_default");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CITY, PHONE, USER_ID, ADDRESS, CONTACT, DISTRICT, PROVINCE, IS_DEFAULT, CREATE_TIME, UPDATE_TIME};

    public UserAddressEntityTableDef() {
        super("", "user_address");
    }

    private UserAddressEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public UserAddressEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new UserAddressEntityTableDef("", "user_address", alias));
    }

}
