package com.vj.orm.core;
import com.vj.orm.config.DBConfig;

import java.sql.Types;

import com.vj.orm.annotation.*;
public class TypeMapper {
    public static String getJavaType(int sqlType) {
        switch (sqlType) {
            case Types.INTEGER:
            case Types.TINYINT:
            case Types.SMALLINT:
                return "int";
            case Types.BIGINT:
                return "long";
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return "String";
            case Types.DATE:
                return "java.sql.Date";
            case Types.TIME:
                return "java.sql.Time";
            case Types.TIMESTAMP:
                return "java.sql.Timestamp";
            case Types.BOOLEAN:
            case Types.BIT:
                return "boolean";
            case Types.DOUBLE:
            case Types.FLOAT:
                return "double";
            default:
                return "Object";
        }
    }
}
