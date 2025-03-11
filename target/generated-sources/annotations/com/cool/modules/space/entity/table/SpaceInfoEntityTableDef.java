package com.cool.modules.space.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SpaceInfoEntityTableDef extends TableDef {

    /**
     * 文件空间信息
     */
    public static final SpaceInfoEntityTableDef SPACE_INFO_ENTITY = new SpaceInfoEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn URL = new QueryColumn(this, "url");

    public final QueryColumn NAME = new QueryColumn(this, "name");

    public final QueryColumn SIZE = new QueryColumn(this, "size");

    public final QueryColumn TYPE = new QueryColumn(this, "type");

    public final QueryColumn FILE_ID = new QueryColumn(this, "file_id");

    public final QueryColumn VERSION = new QueryColumn(this, "version");

    public final QueryColumn FILE_PATH = new QueryColumn(this, "file_path");

    public final QueryColumn CLASSIFY_ID = new QueryColumn(this, "classify_id");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, URL, NAME, SIZE, TYPE, FILE_ID, VERSION, FILE_PATH, CLASSIFY_ID, CREATE_TIME, UPDATE_TIME};

    public SpaceInfoEntityTableDef() {
        super("", "space_info");
    }

    private SpaceInfoEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SpaceInfoEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SpaceInfoEntityTableDef("", "space_info", alias));
    }

}
