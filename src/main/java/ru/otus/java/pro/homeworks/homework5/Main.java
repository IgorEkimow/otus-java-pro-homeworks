package ru.otus.java.pro.homeworks.homework5;

import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) {
        TestLogging real = new TestLogging();
        TestLoggingInterface proxy = (TestLoggingInterface) Proxy.newProxyInstance(TestLogging.class.getClassLoader(), new Class<?>[]{TestLoggingInterface.class}, new LogInvocationHandler(real));

        proxy.calculation(6);
        proxy.calculation(1, 2);
        proxy.calculation(1, 2, "abc");
    }
}