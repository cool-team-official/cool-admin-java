package com.cool.modules.base.entity.sys.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class BaseSysRoleEntityTableDef extends TableDef {

    public static final BaseSysRoleEntityTableDef BASE_SYS_ROLE_ENTITY = new BaseSysRoleEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn NAME = new QueryColumn(this, "name");

    public final QueryColumn LABEL = new QueryColumn(this, "label");

    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    public final QueryColumn RELEVANCE = new QueryColumn(this, "relevance");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn MENU_ID_LIST = new QueryColumn(this, "menu_id_list");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    public final QueryColumn DEPARTMENT_ID_LIST = new QueryColumn(this, "department_id_list");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, LABEL, REMARK, USER_ID, RELEVANCE, CREATE_TIME, MENU_ID_LIST, UPDATE_TIME, DEPARTMENT_ID_LIST};

    public BaseSysRoleEntityTableDef() {
        super("", "base_sys_role");
    }

    private BaseSysRoleEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public BaseSysRoleEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new BaseSysRoleEntityTableDef("", "base_sys_role", alias));
    }

}
