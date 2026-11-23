package ru.otus.java.pro.homeworks.homework9;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityClassMetaData<T> {
    private final Class<T> clazz;
    private final String tableName;
    private final Field idField;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;

    public EntityClassMetaData(Class<T> clazz) {
        this.clazz = clazz;

        if (clazz.isAnnotationPresent(Entity.class)) {
            Entity entity = clazz.getAnnotation(Entity.class);
            this.tableName = entity.name().isEmpty() ? clazz.getSimpleName().toLowerCase() : entity.name();
        } else {
            this.tableName = clazz.getSimpleName().toLowerCase();
        }

        this.allFields = new ArrayList<>();
        this.idField = findIdField();
        if (this.idField == null) {
            throw new IllegalArgumentException("Класс " + clazz.getName() + " должен содержать поле с аннотацией @Id");
        }

        this.fieldsWithoutId = allFields.stream().filter(f -> !f.equals(idField)).collect(Collectors.toList());
    }

    private Field findIdField() {
        Field result = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);
            allFields.add(field);
            if (field.isAnnotationPresent(Id.class) && result == null) {
                result = field;
            }
        }

        return result;
    }

    public Class<T> getClazz() { return clazz; }
    public String getName() { return tableName; }
    public Field getIdField() { return idField; }
    public List<Field> getAllFields() { return allFields; }
    public List<Field> getFieldsWithoutId() { return fieldsWithoutId; }

    public String getColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            Column col = field.getAnnotation(Column.class);
            if (!col.name().isEmpty()) return col.name();
        }

        return field.getName();
    }
}