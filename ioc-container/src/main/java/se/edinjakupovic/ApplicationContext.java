package se.edinjakupovic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.edinjakupovic.cycles.CycleDetector;
import se.edinjakupovic.lifecycle.OnInit;
import se.edinjakupovic.lifecycle.OnShutdown;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ApplicationContext {
    private static final Logger log = LoggerFactory.getLogger(ApplicationContext.class);
    private final Map<Class<?>, Object> classInstanceMap = new HashMap<>();
    private final Map<Class<?>, Method> classFactoryMethodMap = new HashMap<>();

    public static <T> ApplicationContext fromConfiguration(Class<T> configClass) {
        checkConfigPrecondition(configClass);
        CycleDetector.detectCycles(configClass);
        ApplicationContext context = new ApplicationContext();
        context.classInstanceMap.put(ApplicationContext.class, context);
        context.loadInstances(configClass);
        Runtime.getRuntime().addShutdownHook(new Thread(context::runBeforeExitMethods));
        return context;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> clazz) {
        return (T) classInstanceMap.get(clazz);
    }

    private void runBeforeExitMethods() {
        classInstanceMap.values().forEach(this::runBeforeShutdownMethod);
    }


    private <T> void loadInstances(Class<T> configClass) {
        checkConfigPrecondition(configClass);

        if (configClass.isAnnotationPresent(DependsOn.class)) {
            Class<?>[] dependencies = configClass.getAnnotation(DependsOn.class).dependencies();
            for (Class<?> dependency : dependencies) {
                loadInstances(dependency);
            }
        }

        var providerMethods = Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Provides.class))
                .toList();
        checkProviderMethodsPreconditions(providerMethods);

        providerMethods.forEach(method -> classFactoryMethodMap.put(method.getReturnType(), method));
        T instance = provideInstance(configClass);
        providerMethods.forEach(provider -> initInstance(instance, provider));
        classInstanceMap.values().forEach(this::runAfterInitMethod);
    }

    private void checkProviderMethodsPreconditions(List<Method> providerMethods) {
        providerMethods.forEach(this::validateMethodPreconditions);
    }

    private void validateMethodPreconditions(Method method) {
        if (Modifier.isStatic(method.getModifiers()))
            throw new ConfigurationException("Static provider method " + method.getName() + " not supported");
        if (Modifier.isPrivate(method.getModifiers()))
            throw new ConfigurationException("Private provider method " + method.getName() + " not supported");
    }


    private <T> void initInstance(T instance, Method instanceProviderMethod) {
        log.debug("InitInstance [{}] providerType [{}]", instance.getClass().getSimpleName(), instanceProviderMethod.getReturnType().getSimpleName());
        Class<?> returnType = instanceProviderMethod.getReturnType();
        var objectAlreadyInstantiated = classInstanceMap.containsKey(returnType);
        if (objectAlreadyInstantiated)
            return;

        Class<?>[] parameterTypes = instanceProviderMethod.getParameterTypes();
        log.debug("Provider [{}] Parameter Types {} ", instanceProviderMethod.getName(), parameterTypes);

        for (Class<?> type : parameterTypes) {
            if (classInstanceMap.containsKey(type))
                continue;
            Method method = classFactoryMethodMap.get(type);
            if (method == null) {
                log.error("Provider for {} not found", type);
                throw new ConfigurationException("Unable to find provider for parameter " + type);
            }
            initInstance(instance, method);
        }

        Object[] constructorParams = Arrays.stream(parameterTypes).map(classInstanceMap::get).toArray();
        log.debug("Constructor params for {} -> {}", returnType, constructorParams);
        try {
            instanceProviderMethod.setAccessible(true);
            Object objectInstance = instanceProviderMethod.invoke(instance, constructorParams);
            classInstanceMap.put(returnType, objectInstance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ObjectInstantiationException(e);
        }
    }

    private void runAfterInitMethod(Object instance) {
        runZeroArgAnnotatedMethod(instance, OnInit.class);
    }

    private void runBeforeShutdownMethod(Object instance) {
        runZeroArgAnnotatedMethod(instance, OnShutdown.class);
    }

    private void runZeroArgAnnotatedMethod(Object instance, Class<? extends Annotation> annotation) {
        Arrays.stream(instance.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotation))
                .forEach(method -> {
                    try {
                        validateMethodPreconditions(method);
                        method.setAccessible(true);

                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes.length == 1 && parameterTypes[0] == ApplicationContext.class) {
                            method.invoke(instance, this);
                        } else {
                            method.invoke(instance);
                        }
                    } catch (Exception e) {
                        throw new ConfigurationException(
                                "Failed to call method " + method.getName() + " on " + instance.getClass().getSimpleName(), e
                        );
                    }
                });
    }

    private <T> T provideInstance(Class<T> configClass) {
        try {
            return configClass.getDeclaredConstructor().newInstance();
            // TODO: 2023-07-08 Proper Exception Messages
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException e) {
            throw new ObjectInstantiationException("Failed to create class instance for " + configClass.getSimpleName(), e);
        }
    }

    private static <T> void checkConfigPrecondition(Class<T> configClass) {
        if (configClass == null)
            throw new ConfigurationException("Config class was null");
        if (!configClass.isAnnotationPresent(Configuration.class))
            throw new IllegalArgumentException(configClass.getSimpleName() + " not a configuration class");
    }
}
