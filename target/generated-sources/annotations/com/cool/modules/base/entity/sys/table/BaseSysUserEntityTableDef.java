package com.cool.modules.base.entity.sys.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class BaseSysUserEntityTableDef extends TableDef {

    public static final BaseSysUserEntityTableDef BASE_SYS_USER_ENTITY = new BaseSysUserEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn NAME = new QueryColumn(this, "name");

    public final QueryColumn EMAIL = new QueryColumn(this, "email");

    public final QueryColumn PHONE = new QueryColumn(this, "phone");

    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    public final QueryColumn STATUS = new QueryColumn(this, "status");

    public final QueryColumn HEAD_IMG = new QueryColumn(this, "head_img");

    public final QueryColumn NICK_NAME = new QueryColumn(this, "nick_name");

    public final QueryColumn PASSWORD = new QueryColumn(this, "password");

    public final QueryColumn SOCKET_ID = new QueryColumn(this, "socket_id");

    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    public final QueryColumn USERNAME = new QueryColumn(this, "username");

    public final QueryColumn PASSWORD_V = new QueryColumn(this, "password_v");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    public final QueryColumn DEPARTMENT_ID = new QueryColumn(this, "department_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, EMAIL, PHONE, REMARK, STATUS, HEAD_IMG, NICK_NAME, PASSWORD, SOCKET_ID, TENANT_ID, USERNAME, PASSWORD_V, CREATE_TIME, UPDATE_TIME, DEPARTMENT_ID};

    public BaseSysUserEntityTableDef() {
        super("", "base_sys_user");
    }

    private BaseSysUserEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public BaseSysUserEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new BaseSysUserEntityTableDef("", "base_sys_user", alias));
    }

}
