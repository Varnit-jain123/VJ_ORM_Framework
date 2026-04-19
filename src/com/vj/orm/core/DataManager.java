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
            }
        } catch (Exception e) {
            throw new DataException("Failed to end transaction", e);
        }
    }

    public int save(Object obj) throws DataException {
        Class<?> clazz = obj.getClass();
        
        // 1. Get Table Name
        Table tableAnnot = clazz.getAnnotation(Table.class);
        if (tableAnnot == null) throw new DataException("Class " + clazz.getSimpleName() + " is not annotated with @Table");
        String tableName = tableAnnot.name();

        // 2. Identify Fields (Columns) to insert
        List<String> columnNames = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        Object manualPkValue = null;

        for (Field field : clazz.getDeclaredFields()) {
            Column colAnnot = field.getAnnotation(Column.class);
            if (colAnnot == null) continue;

            // Capture manual PK value just in case
            if (field.isAnnotationPresent(PrimaryKey.class) && !field.isAnnotationPresent(AutoIncrement.class)) {
                try {
                    field.setAccessible(true);
                    manualPkValue = field.get(obj);
                } catch (Exception e) {}
            }

            // Skip AutoIncrement fields from INSERT
            if (field.isAnnotationPresent(AutoIncrement.class)) continue;

            columnNames.add(colAnnot.name());
            
            try {
                field.setAccessible(true);
                values.add(field.get(obj));
            } catch (IllegalAccessException e) {
                throw new DataException("Failed to access field " + field.getName(), e);
            }
        }

        // 3. Build SQL
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

        // 4. Execute
        try (PreparedStatement ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.size(); i++) {
                Object val = values.get(i);
                int paramIndex = i + 1;

                // Enhanced Type Handling
                if (val instanceof java.sql.Date) {
                    ps.setDate(paramIndex, (java.sql.Date) val);
                } else if (val instanceof java.util.Date) {
                    ps.setDate(paramIndex, new java.sql.Date(((java.util.Date) val).getTime()));
                } else if (val instanceof String) {
                    ps.setString(paramIndex, (String) val);
                } else if (val instanceof Integer) {
                    ps.setInt(paramIndex, (Integer) val);
                } else {
                    ps.setObject(paramIndex, val);
                }
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) throw new DataException("Save failed, no rows affected.");

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else if (manualPkValue instanceof Integer) {
                    return (Integer) manualPkValue;
                } else {
                    throw new DataException("Save failed, no ID obtained (Generated or Manual).");
                }
            }
        } catch (SQLException e) {
            throw new DataException("SQL Error during save: " + e.getMessage(), e);
        }
    }
}
