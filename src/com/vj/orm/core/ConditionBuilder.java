package com.vj.orm.core;

public class ConditionBuilder<T> {
    private final QueryBuilder<T> queryBuilder;
    private final String columnName;

    public ConditionBuilder(QueryBuilder<T> queryBuilder, String columnName) {
        this.queryBuilder = queryBuilder;
        this.columnName = columnName;
    }

    public QueryBuilder<T> gt(Object value) {
        queryBuilder.appendCondition(columnName + " > ?", value);
        return queryBuilder;
    }

    public QueryBuilder<T> ge(Object value) {
        queryBuilder.appendCondition(columnName + " >= ?", value);
        return queryBuilder;
    }

    public QueryBuilder<T> lt(Object value) {
        queryBuilder.appendCondition(columnName + " < ?", value);
        return queryBuilder;
    }

    public QueryBuilder<T> le(Object value) {
        queryBuilder.appendCondition(columnName + " <= ?", value);
        return queryBuilder;
    }

    public QueryBuilder<T> eq(Object value) {
        queryBuilder.appendCondition(columnName + " = ?", value);
        return queryBuilder;
    }

    public QueryBuilder<T> between(Object v1, Object v2) {
        queryBuilder.appendCondition(columnName + " BETWEEN ? AND ?", v1, v2);
        return queryBuilder;
    }
}
