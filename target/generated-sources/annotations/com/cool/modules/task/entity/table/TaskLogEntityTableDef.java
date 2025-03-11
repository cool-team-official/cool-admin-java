package com.cool.modules.task.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class TaskLogEntityTableDef extends TableDef {

    public static final TaskLogEntityTableDef TASK_LOG_ENTITY = new TaskLogEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn DETAIL = new QueryColumn(this, "detail");

    public final QueryColumn STATUS = new QueryColumn(this, "status");

    public final QueryColumn TASK_ID = new QueryColumn(this, "task_id");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, DETAIL, STATUS, TASK_ID, CREATE_TIME, UPDATE_TIME};

    public TaskLogEntityTableDef() {
        super("", "task_log");
    }

    private TaskLogEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public TaskLogEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new TaskLogEntityTableDef("", "task_log", alias));
    }

}
