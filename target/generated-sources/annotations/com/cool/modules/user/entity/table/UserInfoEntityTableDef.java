package com.cool.modules.user.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class UserInfoEntityTableDef extends TableDef {

    public static final UserInfoEntityTableDef USER_INFO_ENTITY = new UserInfoEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn PHONE = new QueryColumn(this, "phone");

    public final QueryColumn GENDER = new QueryColumn(this, "gender");

    public final QueryColumn STATUS = new QueryColumn(this, "status");

    public final QueryColumn UNIONID = new QueryColumn(this, "unionid");

    public final QueryColumn NICK_NAME = new QueryColumn(this, "nick_name");

    public final QueryColumn PASSWORD = new QueryColumn(this, "password");

    public final QueryColumn AVATAR_URL = new QueryColumn(this, "avatar_url");

    public final QueryColumn LOGIN_TYPE = new QueryColumn(this, "login_type");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, PHONE, GENDER, STATUS, UNIONID, NICK_NAME, PASSWORD, AVATAR_URL, LOGIN_TYPE, CREATE_TIME, UPDATE_TIME};

    public UserInfoEntityTableDef() {
        super("", "user_info");
    }

    private UserInfoEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public UserInfoEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new UserInfoEntityTableDef("", "user_info", alias));
    }

}
