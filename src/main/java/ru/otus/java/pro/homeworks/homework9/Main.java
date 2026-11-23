package ru.otus.java.pro.homeworks.homework9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "")) {

            try (Statement st = conn.createStatement()) {
                st.execute("CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), age INT)");
            }

            DataTemplateJdbc<User> template = new DataTemplateJdbc<>(conn, User.class);

            User user = new User(null, "Alice", 30);
            template.create(user);
            System.out.println("Created user with id = " + user.getId());

            User loaded = template.load(user.getId());
            System.out.println("Loaded: " + loaded.getName() + ", age = " + loaded.getAge());

            loaded.setAge(31);
            template.update(loaded);

            User updated = template.load(loaded.getId());
            System.out.println("After update: age = " + updated.getAge());

            System.out.println("All users: " + template.loadAll().size());
        }
    }
}