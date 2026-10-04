package ru.otus.java.pro.homeworks.homework3;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TestRunner {
    private TestRunner() {}

    public static void run(String testClassName) {
        final Class<?> testClass;
        try {
            testClass = Class.forName(testClassName);
        } catch (ClassNotFoundException e) {
            System.err.println("Не удалось найти класс тестов: " + testClassName);
            System.err.println("Причина: " + e);
            printStatistics(new Statistics());

            return;
        }

        List<Method> tests = findMethods(testClass, Test.class);
        List<Method> beforeMethods = findMethods(testClass, Before.class);
        List<Method> afterMethods = findMethods(testClass, After.class);

        validateMethods(tests, beforeMethods, afterMethods);

        Statistics statistics = new Statistics();
        statistics.total = tests.size();

        for (Method test : tests) {
            runOneTest(testClass, test, beforeMethods, afterMethods, statistics);
        }

        printStatistics(statistics);
    }

    private static List<Method> findMethods(Class<?> testClass, Class<? extends java.lang.annotation.Annotation> annotation) {
        List<Method> methods = new ArrayList<Method>();
        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                methods.add(method);
            }
        }

        methods.sort(Comparator.comparing(Method::getName));

        return methods;
    }

    private static void validateMethods(List<Method> tests, List<Method> beforeMethods, List<Method> afterMethods) {
        List<Method> all = new ArrayList<Method>();
        all.addAll(tests);
        all.addAll(beforeMethods);
        all.addAll(afterMethods);

        for (Method method : all) {
            if (method.getParameterTypes().length != 0) {
                throw new IllegalArgumentException("Метод с аннотацией должен быть без параметров: " + method);
            }
        }
    }

    private static void runOneTest(Class<?> testClass, Method test, List<Method> beforeMethods, List<Method> afterMethods, Statistics statistics) {
        Object testInstance;
        try {
            testInstance = createTestInstance(testClass);
        } catch (Throwable e) {
            statistics.failed++;
            System.err.println("FAIL " + test.getName() + " (не удалось создать экземпляр): " + describe(e));

            return;
        }

        boolean succeeded = true;

        for (Method before : beforeMethods) {
            try {
                invoke(before, testInstance);
            } catch (Throwable e) {
                succeeded = false;
                printFailure("Before " + before.getName() + " для " + test.getName(), e);
            }
        }

        try {
            invoke(test, testInstance);
        } catch (Throwable e) {
            succeeded = false;
            printFailure("Test " + test.getName(), e);
        }

        for (Method after : afterMethods) {
            try {
                invoke(after, testInstance);
            } catch (Throwable e) {
                succeeded = false;
                printFailure("After " + after.getName() + " для " + test.getName(), e);
            }
        }

        if (succeeded) {
            statistics.passed++;
            System.out.println("PASS " + test.getName());
        } else {
            statistics.failed++;
            System.out.println("FAIL " + test.getName());
        }
    }

    private static Object createTestInstance(Class<?> testClass) throws ReflectiveOperationException {
        return testClass.getDeclaredConstructor().newInstance();
    }

    private static void invoke(Method method, Object target) throws Exception {
        method.setAccessible(true);
        try {
            method.invoke(target);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) {
                throw (Exception) cause;
            }

            if (cause instanceof Error) {
                throw (Error) cause;
            }

            throw new RuntimeException(cause);
        }
    }

    private static void printFailure(String phase, Throwable error) {
        System.err.println("  Ошибка в " + phase + ": " + describe(error));
    }

    private static String describe(Throwable error) {
        Throwable cause = error;
        while ((cause instanceof InvocationTargetException) && ((InvocationTargetException) cause).getCause() != null) {
            cause = ((InvocationTargetException) cause).getCause();
        }

        return cause.getClass().getSimpleName() + ": " + cause.getMessage();
    }

    private static void printStatistics(Statistics statistics) {
        System.out.println();
        System.out.println("Всего: " + statistics.total);
        System.out.println("Успешно: " + statistics.passed);
        System.out.println("Упало: " + statistics.failed);
    }

    private static final class Statistics {
        private int total;
        private int passed;
        private int failed;
    }
}