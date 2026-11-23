package ru.otus.java.pro.homeworks.homework5;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LogInvocationHandler implements InvocationHandler {
    private final Object target;
    private final Set<String> loggedMethods = new HashSet<>();

    public LogInvocationHandler(Object target) {
        this.target = target;
        for (Method method : target.getClass().getMethods()) {
            if (method.isAnnotationPresent(Log.class)) {
                loggedMethods.add(signature(method));
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (loggedMethods.contains(signature(method))) {
            System.out.println(buildLogMessage(method, args));
        }

        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private static String signature(Method method) {
        return method.getName() + Arrays.toString(method.getParameterTypes());
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