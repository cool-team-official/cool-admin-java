package com.cool.core.mybatis.pg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
/**
 * PostgreSQL Identity 序列同步服务
 * 解决PostgreSQL 默认的序列机制，序列会自动递增，当手动插入指定id时需调用同步接口，否则id会重复。
 */
@Slf4j
@Service
public class PostgresSequenceSyncService {

    private final JdbcTemplate jdbcTemplate;

    public PostgresSequenceSyncService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void syncIdentitySequences() {
        log.info("⏳ 开始同步 PostgreSQL Identity 序列...");

        // 查询所有 identity 字段
        String identityColumnQuery = """
            SELECT table_schema, table_name, column_name
            FROM information_schema.columns
            WHERE is_identity = 'YES'
              AND table_schema = 'public'
        """;

        List<Map<String, Object>> identityColumns = jdbcTemplate.queryForList(identityColumnQuery);

        for (Map<String, Object> col : identityColumns) {
            String schema = (String) col.get("table_schema");
            String table = (String) col.get("table_name");
            String column = (String) col.get("column_name");

            String fullTable = schema + "." + table;

            // 获取对应的序列名
            String seqNameSql = "SELECT pg_get_serial_sequence(?, ?)";
            String seqName = jdbcTemplate.queryForObject(seqNameSql, String.class, fullTable, column);

            if (seqName == null) {
                log.warn("⚠️ 无法获取序列：{}.{}", table, column);
                continue;
            }

            // 获取当前最大 ID
            Long maxId = jdbcTemplate.queryForObject(
                    String.format("SELECT COALESCE(MAX(%s), 0) FROM %s", column, fullTable),
                    Long.class
            );

            if (maxId != null && maxId > 0) {                // 正确的：setval 有返回值，必须用 queryForObject
                String setvalSql = "SELECT setval(?, ?)";
                Long newVal = jdbcTemplate.queryForObject(setvalSql, Long.class, seqName, maxId);
                log.info("✅ 同步序列 [{}] -> 当前最大 ID: {}", seqName, newVal);
            }
        }

        log.info("✅ PostgreSQL Identity 序列同步完成。");
    }
}