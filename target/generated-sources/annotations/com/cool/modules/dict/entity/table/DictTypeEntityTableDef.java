package com.cool.modules.dict.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class DictTypeEntityTableDef extends TableDef {

    public static final DictTypeEntityTableDef DICT_TYPE_ENTITY = new DictTypeEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn KEY = new QueryColumn(this, "key");

    public final QueryColumn NAME = new QueryColumn(this, "name");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, KEY, NAME, CREATE_TIME, UPDATE_TIME};

    public DictTypeEntityTableDef() {
        super("", "dict_type");
    }

    private DictTypeEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public DictTypeEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new DictTypeEntityTableDef("", "dict_type", alias));
    }

}
