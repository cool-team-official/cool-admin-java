package com.cool.modules.space.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SpaceTypeEntityTableDef extends TableDef {

    /**
     * 图片空间信息分类
     */
    public static final SpaceTypeEntityTableDef SPACE_TYPE_ENTITY = new SpaceTypeEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn NAME = new QueryColumn(this, "name");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, PARENT_ID, CREATE_TIME, UPDATE_TIME};

    public SpaceTypeEntityTableDef() {
        super("", "space_type");
    }

    private SpaceTypeEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SpaceTypeEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SpaceTypeEntityTableDef("", "space_type", alias));
    }

}
