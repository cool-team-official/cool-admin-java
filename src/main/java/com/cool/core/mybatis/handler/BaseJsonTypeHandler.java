package com.cool.core.mybatis.handler;

import com.cool.core.util.DatabaseDialectUtils;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public abstract class BaseJsonTypeHandler<T> extends BaseTypeHandler<T> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (DatabaseDialectUtils.isPostgresql()) {
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(toJson(parameter));
            ps.setObject(i, jsonObject);
        } else {
            ps.setString(i, toJson(parameter));
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        final String json = rs.getString(columnName);
        return StringUtil.noText(json) ? null : parseJson(json);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        final String json = rs.getString(columnIndex);
        return StringUtil.noText(json) ? null : parseJson(json);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        final String json = cs.getString(columnIndex);
        return StringUtil.noText(json) ? null : parseJson(json);
    }

    protected abstract T parseJson(String json);

    protected abstract String toJson(T object);

}
