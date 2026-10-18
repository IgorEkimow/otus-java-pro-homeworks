package ru.otus.java.pro.homeworks.homework5;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LogInvocationHandler implements InvocationHandler {
    private final Object target;

    public LogInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isAnnotationPresent(Log.class)) {
            System.out.println(buildLogMessage(method, args));
        }

        return method.invoke(target, args);
    }

    private String buildLogMessage(Method method, Object[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("executed method: ").append(method.getName());
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                sb.append(", param").append(i + 1).append(": ").append(args[i]);
            }
        }

        return sb.toString();
    }
}