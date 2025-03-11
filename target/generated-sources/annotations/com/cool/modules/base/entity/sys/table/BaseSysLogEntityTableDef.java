package com.cool.modules.base.entity.sys.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class BaseSysLogEntityTableDef extends TableDef {

    public static final BaseSysLogEntityTableDef BASE_SYS_LOG_ENTITY = new BaseSysLogEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn IP = new QueryColumn(this, "ip");

    public final QueryColumn ACTION = new QueryColumn(this, "action");

    public final QueryColumn PARAMS = new QueryColumn(this, "params");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, IP, ACTION, PARAMS, USER_ID, CREATE_TIME, UPDATE_TIME};

    public BaseSysLogEntityTableDef() {
        super("", "base_sys_log");
    }

    private BaseSysLogEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public BaseSysLogEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new BaseSysLogEntityTableDef("", "base_sys_log", alias));
    }

}
