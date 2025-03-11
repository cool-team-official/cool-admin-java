package com.cool.modules.base.entity.sys.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class BaseSysConfEntityTableDef extends TableDef {

    /**
     * 系统配置
     */
    public static final BaseSysConfEntityTableDef BASE_SYS_CONF_ENTITY = new BaseSysConfEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn C_KEY = new QueryColumn(this, "c_key");

    public final QueryColumn C_VALUE = new QueryColumn(this, "c_value");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, C_KEY, C_VALUE, CREATE_TIME, UPDATE_TIME};

    public BaseSysConfEntityTableDef() {
        super("", "base_sys_conf");
    }

    private BaseSysConfEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public BaseSysConfEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new BaseSysConfEntityTableDef("", "base_sys_conf", alias));
    }

}
