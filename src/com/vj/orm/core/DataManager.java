package com.vj.orm.core;

import com.vj.orm.config.*;
import com.vj.orm.annotation.*;
import com.vj.orm.exception.DataException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class DataManager {
    private static DataManager instance = null;
    private DBConfig config;
    private Connection connection;

    private DataManager() {
        try {
            this.config = ConfigReader.readConfig("conf.json");
        } catch (Exception e) {
            System.err.println("Fatal: Could not load conf.json - " + e.getMessage());
        }
    }

    public static DataManager getDataManager() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void begin() throws DataException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = ConnectionManager.getConnection(config);
            }
            connection.setAutoCommit(false);
        } catch (Exception e) {
            throw new DataException("Failed to begin transaction", e);
        }
    }

    public void end() throws DataException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.commit();
                ConnectionManager.closeConnection();
                connection = null; // Reset for next session
            }
        } catch (Exception e) {
            throw new DataException("Failed to end transaction", e);
        }
    }

    // --- Framework Internal Helpers ---

    protected Connection getConnection() {
        return connection;
    }

    protected String getTableName(Class<?> clazz) throws DataException {
        Table tableAnnot = clazz.getAnnotation(Table.class);
        if (tableAnnot == null) throw new DataException("Class " + clazz.getSimpleName() + " is not annotated with @Table");
        return tableAnnot.name();
    }

    protected void setParameter(PreparedStatement ps, int index, Object val) throws SQLException {
        if (val instanceof java.sql.Date) {
            ps.setDate(index, (java.sql.Date) val);
        } else if (val instanceof java.util.Date) {
            ps.setDate(index, new java.sql.Date(((java.util.Date) val).getTime()));
        } else if (val instanceof String) {
            ps.setString(index, (String) val);
        } else if (val instanceof Integer) {
            ps.setInt(index, (Integer) val);
        } else {
            ps.setObject(index, val);
        }
    }

    protected <T> T mapRow(ResultSet rs, Class<T> clazz) throws Exception {
        T obj = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            Column colAnnot = field.getAnnotation(Column.class);
            if (colAnnot == null) continue;

            String columnName = colAnnot.name();
            Object value = rs.getObject(columnName);

            if (value != null) {
                field.setAccessible(true);
                Class<?> type = field.getType();
                if (type == int.class || type == Integer.class) {
                    field.set(obj, rs.getInt(columnName));
                } else if (type == String.class) {
                    field.set(obj, rs.getString(columnName));
                } else if (type == java.sql.Date.class) {
                    field.set(obj, rs.getDate(columnName));
                } else {
                    field.set(obj, value);
                }
            }
        }
        return obj;
    }

    // --- CRUD Operations ---

    public int save(Object obj) throws DataException {
        Class<?> clazz = obj.getClass();
        String tableName = getTableName(clazz);

        List<String> columnNames = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        Object manualPkValue = null;

        for (Field field : clazz.getDeclaredFields()) {
            Column colAnnot = field.getAnnotation(Column.class);
            if (colAnnot == null) continue;

            if (field.isAnnotationPresent(PrimaryKey.class) && !field.isAnnotationPresent(AutoIncrement.class)) {
                try {
                    field.setAccessible(true);
                    manualPkValue = field.get(obj);
                } catch (Exception e) {}
            }

            if (field.isAnnotationPresent(AutoIncrement.class)) continue;

            columnNames.add(colAnnot.name());
            try {
                field.setAccessible(true);
                values.add(field.get(obj));
            } catch (IllegalAccessException e) {
                throw new DataException("Failed to access field " + field.getName(), e);
            }
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder placeholders = new StringBuilder(" VALUES (");
        for (int i = 0; i < columnNames.size(); i++) {
            sql.append(columnNames.get(i));
            placeholders.append("?");
            if (i < columnNames.size() - 1) {
                sql.append(", ");
                placeholders.append(", ");
            }
        }
        sql.append(")").append(placeholders).append(")");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.size(); i++) {
                setParameter(ps, i + 1, values.get(i));
            }
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) throw new DataException("Save failed, no rows affected.");

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else if (manualPkValue instanceof Integer) {
                    return (Integer) manualPkValue;
                } else {
                    throw new DataException("Save failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DataException("SQL Error during save: " + e.getMessage(), e);
        }
    }

    public void update(Object obj) throws DataException {
        Class<?> clazz = obj.getClass();
        String tableName = getTableName(clazz);

        Field pkField = null;
        String pkColumnName = null;
        List<String> setColumns = new ArrayList<>();
        List<Object> setValues = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            Column colAnnot = field.getAnnotation(Column.class);
            if (colAnnot == null) continue;

            if (field.isAnnotationPresent(PrimaryKey.class)) {
                pkField = field;
                pkColumnName = colAnnot.name();
            } else {
                setColumns.add(colAnnot.name());
                try {
                    field.setAccessible(true);
                    setValues.add(field.get(obj));
                } catch (IllegalAccessException e) {
                    throw new DataException("Failed to access field " + field.getName(), e);
                }
            }
        }

        if (pkField == null) throw new DataException("Class " + clazz.getSimpleName() + " has no @PrimaryKey field.");

        Object pkValue;
        try {
            pkField.setAccessible(true);
            pkValue = pkField.get(obj);
        } catch (IllegalAccessException e) {
            throw new DataException("Failed to access primary key value", e);
        }

        if (pkValue == null) throw new DataException("Update failed: Primary key value is null.");

        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        for (int i = 0; i < setColumns.size(); i++) {
            sql.append(setColumns.get(i)).append("=?");
            if (i < setColumns.size() - 1) sql.append(", ");
        }
        sql.append(" WHERE ").append(pkColumnName).append("=?");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int i = 0;
            for (; i < setValues.size(); i++) {
                setParameter(ps, i + 1, setValues.get(i));
            }
            setParameter(ps, i + 1, pkValue);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) throw new DataException("Update failed: Record not found.");
        } catch (SQLException e) {
            throw new DataException("SQL Error during update: " + e.getMessage(), e);
        }
    }

    public void delete(Class<?> clazz, Object primaryKey) throws DataException {
        String tableName = getTableName(clazz);
        String pkColumnName = null;

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                Column colAnnot = field.getAnnotation(Column.class);
                if (colAnnot != null) {
                    pkColumnName = colAnnot.name();
                    break;
                }
            }
        }

        if (pkColumnName == null) throw new DataException("Class " + clazz.getSimpleName() + " has no @PrimaryKey field.");
        if (primaryKey == null) throw new DataException("Delete failed: Primary key value is null.");

        String sql = "DELETE FROM " + tableName + " WHERE " + pkColumnName + "=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setParameter(ps, 1, primaryKey);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) throw new DataException("Delete failed: Record not found.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new DataException("Delete failed: Record is referenced by other items (Foreign Key constraint).", e);
            }
            throw new DataException("SQL Error during delete: " + e.getMessage(), e);
        }
    }

    public <T> QueryBuilder<T> query(Class<T> clazz) throws DataException {
        return new QueryBuilder<>(this, clazz);
    }
}
