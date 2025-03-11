package com.cool.modules.base.entity.sys.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class BaseSysRoleDepartmentEntityTableDef extends TableDef {

    public static final BaseSysRoleDepartmentEntityTableDef BASE_SYS_ROLE_DEPARTMENT_ENTITY = new BaseSysRoleDepartmentEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn ROLE_ID = new QueryColumn(this, "role_id");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ROLE_ID, CREATE_TIME, UPDATE_TIME, DEPARTMENT_ID};

    public BaseSysRoleDepartmentEntityTableDef() {
        super("", "base_sys_role_department");
    }

    private BaseSysRoleDepartmentEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public BaseSysRoleDepartmentEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new BaseSysRoleDepartmentEntityTableDef("", "base_sys_role_department", alias));
    }

}
