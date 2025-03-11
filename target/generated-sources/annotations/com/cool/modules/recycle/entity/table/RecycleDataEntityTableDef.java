package com.cool.modules.recycle.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class RecycleDataEntityTableDef extends TableDef {

    /**
     * 数据回收站 软删除的时候数据会回收到该表
     */
    public static final RecycleDataEntityTableDef RECYCLE_DATA_ENTITY = new RecycleDataEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn URL = new QueryColumn(this, "url");

    public final QueryColumn DATA = new QueryColumn(this, "data");

    public final QueryColumn COUNT = new QueryColumn(this, "count");

    public final QueryColumn PARAMS = new QueryColumn(this, "params");

    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn ENTITY_INFO = new QueryColumn(this, "entity_info");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, URL, DATA, COUNT, PARAMS, USER_ID, CREATE_TIME, ENTITY_INFO, UPDATE_TIME};

    public RecycleDataEntityTableDef() {
        super("", "recycle_data");
    }

    private RecycleDataEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public RecycleDataEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new RecycleDataEntityTableDef("", "recycle_data", alias));
    }

}
