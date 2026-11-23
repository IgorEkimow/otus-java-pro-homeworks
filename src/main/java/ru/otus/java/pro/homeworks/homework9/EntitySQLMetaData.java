package ru.otus.java.pro.homeworks.homework9;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class EntitySQLMetaData<T> {
    private final EntityClassMetaData<T> meta;

    public EntitySQLMetaData(EntityClassMetaData<T> meta) {
        this.meta = meta;
    }

    public String getSelectAllSql() {
        return "SELECT * FROM " + meta.getName();
    }

    public String getSelectByIdSql() {
        String idCol = meta.getColumnName(meta.getIdField());

        return String.format("SELECT * FROM %s WHERE %s = ?", meta.getName(), idCol);
    }

    public String getInsertSql() {
        String columns = meta.getFieldsWithoutId().stream().map(meta::getColumnName).collect(Collectors.joining(", "));
        String placeholders = meta.getFieldsWithoutId().stream().map(f -> "?").collect(Collectors.joining(", "));

        return String.format("INSERT INTO %s (%s) VALUES (%s)", meta.getName(), columns, placeholders);
    }

    public String getUpdateSql() {
        String setClause = meta.getFieldsWithoutId().stream().map(f -> meta.getColumnName(f) + " = ?").collect(Collectors.joining(", "));
        String idCol = meta.getColumnName(meta.getIdField());

        return String.format("UPDATE %s SET %s WHERE %s = ?", meta.getName(), setClause, idCol);
    }
}