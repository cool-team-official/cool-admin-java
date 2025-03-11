package com.cool.core.leaf.segment.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class LeafAllocEntityTableDef extends TableDef {

    public static final LeafAllocEntityTableDef LEAF_ALLOC_ENTITY = new LeafAllocEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn KEY = new QueryColumn(this, "key");

    public final QueryColumn STEP = new QueryColumn(this, "step");

    public final QueryColumn MAX_ID = new QueryColumn(this, "max_id");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, KEY, STEP, MAX_ID, CREATE_TIME, UPDATE_TIME, DESCRIPTION};

    public LeafAllocEntityTableDef() {
        super("", "leaf_alloc");
    }

    private LeafAllocEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public LeafAllocEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new LeafAllocEntityTableDef("", "leaf_alloc", alias));
    }

}
