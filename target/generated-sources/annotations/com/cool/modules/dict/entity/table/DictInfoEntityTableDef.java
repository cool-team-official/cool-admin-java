package com.cool.modules.dict.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class DictInfoEntityTableDef extends TableDef {

    public static final DictInfoEntityTableDef DICT_INFO_ENTITY = new DictInfoEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn NAME = new QueryColumn(this, "name");

    public final QueryColumn VALUE = new QueryColumn(this, "value");

    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    public final QueryColumn TYPE_ID = new QueryColumn(this, "type_id");

    public final QueryColumn ORDER_NUM = new QueryColumn(this, "order_num");

    public final QueryColumn PARENT_ID = new QueryColumn(this, "parent_id");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, VALUE, REMARK, TYPE_ID, ORDER_NUM, PARENT_ID, CREATE_TIME, UPDATE_TIME};

    public DictInfoEntityTableDef() {
        super("", "dict_info");
    }

    private DictInfoEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public DictInfoEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new DictInfoEntityTableDef("", "dict_info", alias));
    }

}
