package ru.otus.java.pro.homeworks.homework13.appcontainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import ru.otus.java.pro.homeworks.homework13.appcontainer.api.AppComponent;
import ru.otus.java.pro.homeworks.homework13.appcontainer.api.AppComponentsContainer;
import ru.otus.java.pro.homeworks.homework13.appcontainer.api.AppComponentsContainerConfig;

public final class AppComponentsContainerImpl implements AppComponentsContainer {
    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        List<Method> factoryMethods = Arrays.stream(configClass.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(AppComponent.class))
            .filter(method -> !method.isBridge() && !method.isSynthetic())
            .sorted(Comparator.comparingInt((Method method) -> method.getAnnotation(AppComponent.class).order()).thenComparing(Method::toGenericString))
            .toList();

        Set<String> names = new HashSet<>();
        for (Method method : factoryMethods) {
            String name = method.getAnnotation(AppComponent.class).name();

            if (name.isBlank() || !names.add(name)) {
                throw new IllegalArgumentException("Empty or duplicate component name: " + name);
            }

            if (method.getReturnType() == void.class) {
                throw new IllegalArgumentException("Component factory must return a value: " + method);
            }
        }

        Object config = createConfig(configClass);
        for (Method method : factoryMethods) {
            createComponent(config, method);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        Objects.requireNonNull(configClass, "configClass");
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private Object createConfig(Class<?> configClass) {
        try {
            var constructor = configClass.getDeclaredConstructor();
            if (!constructor.trySetAccessible()) {
                throw new IllegalArgumentException("Config constructor is not accessible: " + configClass.getName());
            }

            return constructor.newInstance();
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Config constructor failed: " + configClass.getName(), e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Config must have a no-argument constructor: " + configClass.getName(), e);
        }
    }

    private void createComponent(Object config, Method method) {
        String name = method.getAnnotation(AppComponent.class).name();
        Object[] arguments;
        try {
            arguments = Arrays.stream(method.getParameterTypes()).map(this::getAppComponent).toArray();
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Cannot resolve dependencies for component '" + name + "'. Check parameter types and @AppComponent order.", e);
        }

        try {
            if (!method.trySetAccessible()) {
                throw new IllegalArgumentException("Component factory is not accessible: " + method);
            }

            Object component = method.invoke(config, arguments);

            if (component == null) {
                throw new IllegalStateException("Component factory returned null: " + name);
            }

            appComponents.add(component);
            appComponentsByName.put(name, component);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Component factory failed: " + name, e.getCause());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot invoke component factory: " + method, e);
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        Objects.requireNonNull(componentClass, "componentClass");
        C found = null;
        for (Object component : appComponents) {
            if (componentClass.isInstance(component)) {
                if (found != null) {
                    throw new IllegalArgumentException("Multiple components match type: " + componentClass.getName());
                }

                found = componentClass.cast(component);
            }
        }

        if (found == null) {
            throw new IllegalArgumentException("Component not found for type: " + componentClass.getName());
        }

        return found;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(String componentName) {
        Objects.requireNonNull(componentName, "componentName");
        Object component = appComponentsByName.get(componentName);
        if (component == null) {
            throw new IllegalArgumentException("Component not found by name: " + componentName);
        }

        return (C) component;
    }
}