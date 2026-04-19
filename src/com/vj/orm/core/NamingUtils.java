package com.vj.orm.core;

public class NamingUtils {
    public static String toPascalCase(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) return snakeCase;
        StringBuilder sb = new StringBuilder();
        for (String part : snakeCase.split("_")) {
            if (part.length() > 0) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                sb.append(part.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    public static String toCamelCase(String snakeCase) {
        String pascal = toPascalCase(snakeCase);
        if (pascal == null || pascal.isEmpty()) return pascal;
        return Character.toLowerCase(pascal.charAt(0)) + pascal.substring(1);
    }
}
