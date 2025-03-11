package com.cool.modules.user.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class UserWxEntityTableDef extends TableDef {

    public static final UserWxEntityTableDef USER_WX_ENTITY = new UserWxEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn CITY = new QueryColumn(this, "city");

    public final QueryColumn TYPE = new QueryColumn(this, "type");

    public final QueryColumn GENDER = new QueryColumn(this, "gender");

    public final QueryColumn OPENID = new QueryColumn(this, "openid");

    public final QueryColumn COUNTRY = new QueryColumn(this, "country");

    public final QueryColumn UNIONID = new QueryColumn(this, "unionid");

    public final QueryColumn LANGUAGE = new QueryColumn(this, "language");

    public final QueryColumn NICK_NAME = new QueryColumn(this, "nick_name");

    public final QueryColumn PROVINCE = new QueryColumn(this, "province");

    public final QueryColumn AVATAR_URL = new QueryColumn(this, "avatar_url");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CITY, TYPE, GENDER, OPENID, COUNTRY, UNIONID, LANGUAGE, NICK_NAME, PROVINCE, AVATAR_URL, CREATE_TIME, UPDATE_TIME};

    public UserWxEntityTableDef() {
        super("", "user_wx");
    }

    private UserWxEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public UserWxEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new UserWxEntityTableDef("", "user_wx", alias));
    }

}
