package com.cool.core.util;
import com.cool.core.exception.CoolPreconditions;
import com.tangzc.autotable.core.constants.DatabaseDialect;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * 获取数据库方言
 */
public class DatabaseDialectUtils {
    private static String dialect;

    public static String getDatabaseDialect() {
        if (dialect == null) {
            dialect = determineDatabaseType();
        }
        return dialect;
    }

    private static String determineDatabaseType() {
        // 从 DataSource 获取连接
        DataSource dataSource = SpringContextUtils.getBean(DataSource.class);
        try (Connection connection = dataSource.getConnection()) {
            // 获取元数据
            DatabaseMetaData metaData = connection.getMetaData();
            String productName = metaData.getDatabaseProductName();

            return inferDatabaseTypeFromProductName(productName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to determine database dialect", e);
        }
    }

    private static String inferDatabaseTypeFromProductName(String productName) {
        if (productName.startsWith(DatabaseDialect.MySQL)) {
            return DatabaseDialect.MySQL;
        } else if (productName.startsWith(DatabaseDialect.PostgreSQL)) {
            return DatabaseDialect.PostgreSQL;
        } else if (productName.startsWith(DatabaseDialect.SQLite)) {
            return DatabaseDialect.SQLite;
        }
        CoolPreconditions.alwaysThrow("暂不支持!");
        return "unknown";
    }
}
