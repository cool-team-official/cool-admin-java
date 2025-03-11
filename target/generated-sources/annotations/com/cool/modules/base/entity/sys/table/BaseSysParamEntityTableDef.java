package com.cool.modules.base.entity.sys.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class BaseSysParamEntityTableDef extends TableDef {

    public static final BaseSysParamEntityTableDef BASE_SYS_PARAM_ENTITY = new BaseSysParamEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn DATA = new QueryColumn(this, "data");

    public final QueryColumn NAME = new QueryColumn(this, "name");

    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    public final QueryColumn KEY_NAME = new QueryColumn(this, "key_name");

    public final QueryColumn DATA_TYPE = new QueryColumn(this, "data_type");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, DATA, NAME, REMARK, KEY_NAME, DATA_TYPE, CREATE_TIME, UPDATE_TIME};

    public BaseSysParamEntityTableDef() {
        super("", "base_sys_param");
    }

    private BaseSysParamEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public BaseSysParamEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new BaseSysParamEntityTableDef("", "base_sys_param", alias));
    }

}
