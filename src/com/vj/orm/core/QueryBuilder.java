package com.vj.orm.core;

import com.vj.orm.annotation.Column;
import com.vj.orm.exception.DataException;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class QueryBuilder<T> {
    private final DataManager dm;
    private final Class<T> clazz;
    private final StringBuilder whereClause = new StringBuilder();
    private final List<Object> params = new ArrayList<>();

    public QueryBuilder(DataManager dm, Class<T> clazz) {
        this.dm = dm;
        this.clazz = clazz;
    }

    private String getColumnName(String fieldName) throws DataException {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            Column col = field.getAnnotation(Column.class);
            if (col == null) throw new DataException("Field " + fieldName + " is not annotated with @Column");
            return col.name();
        } catch (NoSuchFieldException e) {
            throw new DataException("Field " + fieldName + " does not exist in class " + clazz.getSimpleName());
        }
    }

    public ConditionBuilder<T> where(String fieldName) throws DataException {
        whereClause.append(" WHERE ");
        return new ConditionBuilder<>(this, getColumnName(fieldName));
    }

    public ConditionBuilder<T> and(String fieldName) throws DataException {
        whereClause.append(" AND ");
        return new ConditionBuilder<>(this, getColumnName(fieldName));
    }

    public ConditionBuilder<T> or(String fieldName) throws DataException {
        whereClause.append(" OR ");
        return new ConditionBuilder<>(this, getColumnName(fieldName));
    }

    protected void appendCondition(String sqlFragment, Object... values) {
        whereClause.append(sqlFragment);
        for (Object val : values) {
            params.add(val);
        }
    }

    public List<T> list() throws DataException {
        // Cache Hit Check (Only for Select All queries for now)
        if (whereClause.length() == 0 && dm.isCacheable(clazz)) {
            List<Object> cached = dm.getCachedList(clazz);
            if (cached != null) {
                System.out.println("ORM: Cache Hit for " + clazz.getSimpleName());
                List<T> results = new ArrayList<>();
                for (Object o : cached) {
                    results.add(dm.cloneObject((T) o));
                }
                return results;
            }
        }

        String tableName = dm.getTableName(clazz);
        String sql = "SELECT * FROM " + tableName + whereClause.toString();
        List<T> results = new ArrayList<>();

        try (PreparedStatement ps = dm.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                dm.setParameter(ps, i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(dm.mapRow(rs, clazz));
                }
            }
        } catch (Exception e) {
            throw new DataException("Error executing filtered query for " + clazz.getSimpleName() + ": " + e.getMessage(), e);
        }
        return results;
    }
}
