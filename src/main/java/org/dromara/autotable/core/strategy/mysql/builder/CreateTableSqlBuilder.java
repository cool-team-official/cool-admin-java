package org.dromara.autotable.core.strategy.mysql.builder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.autotable.annotation.enums.IndexTypeEnum;
import org.dromara.autotable.core.strategy.IndexMetadata;
import org.dromara.autotable.core.strategy.mysql.data.MysqlColumnMetadata;
import org.dromara.autotable.core.strategy.mysql.data.MysqlTableMetadata;
import org.dromara.autotable.core.utils.StringConnectHelper;
import org.dromara.autotable.core.utils.StringUtils;

/**
 * @author don
 * 临时解决：https://gitee.com/dromara/auto-table/issues/IBPYHU
 */
@Slf4j
public class CreateTableSqlBuilder {

    /**
     * 构建创建新表的SQL
     *
     * @param mysqlTableMetadata 参数
     * @return sql
     */
    public static String buildSql(MysqlTableMetadata mysqlTableMetadata) {

        String name = mysqlTableMetadata.getTableName();
        List<MysqlColumnMetadata> mysqlColumnMetadataList = mysqlTableMetadata.getColumnMetadataList();
        List<IndexMetadata> indexMetadataList = mysqlTableMetadata.getIndexMetadataList();
        String collate = mysqlTableMetadata.getCollate();
        String engine = mysqlTableMetadata.getEngine();
        String characterSet = mysqlTableMetadata.getCharacterSet();
        String comment = mysqlTableMetadata.getComment();

        // 记录所有修改项，（利用数组结构，便于添加,分割）
        List<String> addItems = new ArrayList<>();

        // 获取所有主键（至于表字段处理之前，为了主键修改notnull）
        List<String> primaries = new ArrayList<>();
        mysqlColumnMetadataList.forEach(columnData -> {
            // 判断是主键，自动设置为NOT NULL，并记录
            if (columnData.isPrimary()) {
                columnData.setNotNull(true);
                primaries.add(columnData.getName());
            }
        });

        // 表字段处理
        addItems.add(
            mysqlColumnMetadataList.stream()
                .sorted(Comparator.comparingInt(MysqlColumnMetadata::getPosition))
                // 拼接每个字段的sql片段
                .map(ColumnSqlBuilder::buildSql)
                .collect(Collectors.joining(","))
        );


        // 主键
        if (!primaries.isEmpty()) {
            String primaryKeySql = getPrimaryKeySql(primaries);
            addItems.add(primaryKeySql);
        }

        // 索引
        addItems.add(
            indexMetadataList.stream()
                // 例子： UNIQUE INDEX `unique_name_age`(`name` ASC, `age` DESC) COMMENT '姓名、年龄索引' USING BTREE
                .map(CreateTableSqlBuilder::getIndexSql)
                // 同类型的索引，排在一起，SQL美化
                .sorted()
                .collect(Collectors.joining(","))
        );

        List<String> tableProperties = getTableProperties(engine, characterSet, collate, comment);

        // 组合sql: 过滤空字符项，逗号拼接
        String addSql = addItems.stream()
            .filter(StringUtils::hasText)
            .collect(Collectors.joining(","));
        String propertiesSql = tableProperties.stream()
            .filter(StringUtils::hasText)
            .collect(Collectors.joining(","));

        return "CREATE TABLE `{tableName}` ({addItems}) {tableProperties};"
            .replace("{tableName}", name)
            .replace("{addItems}", addSql)
            .replace("{tableProperties}", propertiesSql);
    }

    public static List<String> getTableProperties(String engine, String characterSet, String collate, String comment) {
        List<String> tableProperties = new ArrayList<>();

        // 引擎
        if (StringUtils.hasText(engine)) {
            tableProperties.add("ENGINE = " + engine);
        }
        // 字符集
        if (StringUtils.hasText(characterSet)) {
            tableProperties.add("CHARACTER SET = " + characterSet);
        }
        // 排序
        if (StringUtils.hasText(collate)) {
            tableProperties.add("COLLATE = " + collate);
        }
        // 备注
        if (StringUtils.hasText(comment)) {
            tableProperties.add(
                "COMMENT = '{comment}'"
                    .replace("{comment}", comment)
            );
        }
        return tableProperties;
    }

    public static String getIndexSql(IndexMetadata indexMetadata) {
        // 例子： UNIQUE INDEX `unique_name_age`(`name` ASC, `age` DESC) COMMENT '姓名、年龄索引',
        return StringConnectHelper.newInstance("{indexType} INDEX {indexName}({columns}) {method} {indexComment}")
            .replace("{indexType}", indexMetadata.getType() == IndexTypeEnum.UNIQUE ? "UNIQUE" : "")
            .replace("{indexName}", indexMetadata.getName())
            .replace("{columns}", () -> {
                List<IndexMetadata.IndexColumnParam> columnParams = indexMetadata.getColumns();
                return columnParams.stream().map(column ->
                    // 例：name ASC
                    "`{column}` {sortMode}"
                        .replace("{column}", column.getColumn())
                        .replace("{sortMode}", column.getSort() != null ? column.getSort().name() : "")
                ).collect(Collectors.joining(","));
            })
            .replace("{method}", StringUtils.hasText(indexMetadata.getMethod()) ? "USING " + indexMetadata.getMethod() : "")
            .replace("{indexComment}", StringUtils.hasText(indexMetadata.getComment()) ? "COMMENT '" + indexMetadata.getComment() + "'" : "")
            .toString();
    }

    public static String getPrimaryKeySql(List<String> primaries) {
        return "PRIMARY KEY ({primaries})"
            .replace(
                "{primaries}",
                String.join(",", primaries)
            );
    }
}
