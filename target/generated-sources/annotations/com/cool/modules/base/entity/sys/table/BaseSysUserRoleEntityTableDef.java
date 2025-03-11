package com.cool.modules.base.entity.sys.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class BaseSysUserRoleEntityTableDef extends TableDef {

    public static final BaseSysUserRoleEntityTableDef BASE_SYS_USER_ROLE_ENTITY = new BaseSysUserRoleEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn ROLE_ID = new QueryColumn(this, "role_id");

    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ROLE_ID, USER_ID, CREATE_TIME, UPDATE_TIME};

    public BaseSysUserRoleEntityTableDef() {
        super("", "base_sys_user_role");
    }

    private BaseSysUserRoleEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public BaseSysUserRoleEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new BaseSysUserRoleEntityTableDef("", "base_sys_user_role", alias));
    }

}
