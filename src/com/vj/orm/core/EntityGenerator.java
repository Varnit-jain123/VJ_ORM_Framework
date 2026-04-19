package com.vj.orm.core;
import com.vj.orm.config.DBConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import com.vj.orm.annotation.*;
public class EntityGenerator {
    private Connection connection;

    public EntityGenerator(Connection connection) {
        this.connection = connection;
    }

    public void generateEntities() throws SQLException, IOException {
        DatabaseMetaData metaData = connection.getMetaData();
        String catalog = connection.getCatalog();

        // Ensure DTO_files directory exists
        File directory = new File("DTO_files");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (ResultSet tables = metaData.getTables(catalog, null, null, new String[]{"TABLE"})) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                generateEntityForTable(metaData, catalog, tableName);
            }
        }
    }

    private void generateEntityForTable(DatabaseMetaData metaData, String catalog, String tableName) throws SQLException, IOException {
        String className = NamingUtils.toPascalCase(tableName);
        
        // Fetch Primary Keys
        Set<String> primaryKeys = new HashSet<>();
        try (ResultSet pks = metaData.getPrimaryKeys(catalog, null, tableName)) {
            while (pks.next()) {
                primaryKeys.add(pks.getString("COLUMN_NAME"));
            }
        }

        // Fetch Foreign Keys (Imported Keys)
        Map<String, String[]> foreignKeys = new HashMap<>(); // colName -> [parentTable, parentCol]
        try (ResultSet fks = metaData.getImportedKeys(catalog, null, tableName)) {
            while (fks.next()) {
                String fkCol = fks.getString("FKCOLUMN_NAME");
                String pkTab = fks.getString("PKTABLE_NAME");
                String pkCol = fks.getString("PKCOLUMN_NAME");
                foreignKeys.put(fkCol, new String[]{pkTab, pkCol});
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("package DTO_files;\n\n");
        sb.append("import com.vj.orm.annotation.*;\n\n");
        sb.append("@Table(name=\"").append(tableName).append("\")\n");
        sb.append("public class ").append(className).append(" {\n");

        List<ColumnInfo> columnList = new ArrayList<>();

        try (ResultSet columns = metaData.getColumns(catalog, null, tableName, null)) {
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                int sqlType = columns.getInt("DATA_TYPE");
                String javaType = TypeMapper.getJavaType(sqlType);
                String fieldName = NamingUtils.toCamelCase(columnName);
                String isAutoIncrement = columns.getString("IS_AUTOINCREMENT");

                columnList.add(new ColumnInfo(columnName, fieldName, javaType));

                if (primaryKeys.contains(columnName)) {
                    sb.append("    @PrimaryKey\n");
                }
                if (foreignKeys.containsKey(columnName)) {
                    String[] fkDetails = foreignKeys.get(columnName);
                    sb.append("    @ForeignKey(parent=\"").append(fkDetails[0]).append("\", column=\"").append(fkDetails[1]).append("\")\n");
                }
                if ("YES".equalsIgnoreCase(isAutoIncrement)) {
                    sb.append("    @AutoIncrement\n");
                }
                sb.append("    @Column(name=\"").append(columnName).append("\")\n");
                sb.append("    public ").append(javaType).append(" ").append(fieldName).append(";\n\n");
            }
        }

        // Add Getters and Setters
        for (ColumnInfo col : columnList) {
            String methodNameSuffix = NamingUtils.toPascalCase(col.fieldName);
            
            // Getter
            sb.append("    public ").append(col.javaType).append(" get").append(methodNameSuffix).append("() {\n");
            sb.append("        return this.").append(col.fieldName).append(";\n");
            sb.append("    }\n\n");

            // Setter
            sb.append("    public void set").append(methodNameSuffix).append("(").append(col.javaType).append(" ").append(col.fieldName).append(") {\n");
            sb.append("        this.").append(col.fieldName).append(" = ").append(col.fieldName).append(";\n");
            sb.append("    }\n\n");
        }

        sb.append("}\n");

        String fileName = "DTO_files/" + className + ".java";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(sb.toString());
        }
        System.out.println("Generated: " + fileName);
    }
    private static class ColumnInfo {
        String columnName;
        String fieldName;
        String javaType;

        ColumnInfo(String columnName, String fieldName, String javaType) {
            this.columnName = columnName;
            this.fieldName = fieldName;
            this.javaType = javaType;
        }
    }
}
