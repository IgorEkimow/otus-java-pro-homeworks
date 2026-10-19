package ru.otus.java.pro.homeworks.homework9;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataTemplateJdbc<T> {
    private final EntityClassMetaData<T> classMeta;
    private final EntitySQLMetaData<T> sqlMeta;
    private final Connection connection;

    public DataTemplateJdbc(Connection connection, Class<T> clazz) {
        this.connection = connection;
        this.classMeta = new EntityClassMetaData<>(clazz);
        this.sqlMeta = new EntitySQLMetaData<>(classMeta);
    }

    public void create(T objectData) {
        String sql = sqlMeta.getInsertSql();
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int index = 1;
            for (Field field : classMeta.getFieldsWithoutId()) {
                ps.setObject(index++, field.get(objectData));
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    classMeta.getIdField().set(objectData, rs.getObject(1));
                }
            }
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException("Ошибка при выполнении INSERT для " + classMeta.getName(), e);
        }
    }

    public void update(T objectData) {
        String sql = sqlMeta.getUpdateSql();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            for (Field field : classMeta.getFieldsWithoutId()) {
                ps.setObject(index++, field.get(objectData));
            }

            ps.setObject(index, classMeta.getIdField().get(objectData));
            ps.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException("Ошибка при выполнении UPDATE для " + classMeta.getName(), e);
        }
    }

    public T load(long id) {
        String sql = sqlMeta.getSelectByIdSql();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    T instance = classMeta.getClazz().getDeclaredConstructor().newInstance();
                    for (Field field : classMeta.getAllFields()) {
                        String colName = classMeta.getColumnName(field);
                        Object value = rs.getObject(colName);
                        field.set(instance, value);
                    }

                    return instance;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении SELECT для " + classMeta.getName(), e);
        }

        return null;
    }

    public List<T> loadAll() {
        String sql = sqlMeta.getSelectAllSql();
        List<T> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                T instance = classMeta.getClazz().getDeclaredConstructor().newInstance();
                for (Field field : classMeta.getAllFields()) {
                    field.set(instance, rs.getObject(classMeta.getColumnName(field)));
                }

                result.add(instance);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении SELECT ALL для " + classMeta.getName(), e);
        }
        return result;
    }
}