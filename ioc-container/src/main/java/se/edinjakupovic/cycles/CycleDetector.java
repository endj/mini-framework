package se.edinjakupovic.cycles;

import se.edinjakupovic.ConfigurationException;
import se.edinjakupovic.DependsOn;
import se.edinjakupovic.Provides;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CycleDetector {

    private static DependencyAggregator configClassDependencies(Class<?> clazz,
                                                                DependencyAggregator aggregator,
                                                                Set<Class<?>> visited) {
        visited.add(clazz);
        Class<?>[] dependantConfigClasses = clazz.isAnnotationPresent(DependsOn.class)
                ? clazz.getAnnotation(DependsOn.class).dependencies()
                : new Class<?>[0];
        aggregator.configClassDependencies.put(clazz, dependantConfigClasses);
        Map<Class<?>, Class<?>[]> dependenciesForProviderMethods = getDependenciesForProviderMethods(clazz);
        for (var entry : dependenciesForProviderMethods.entrySet()) {
            if (aggregator.instanceClassDependencies.containsKey(entry.getKey()))
                throw new ConfigurationException("Only 1 provider per type allowed");
            aggregator.instanceClassDependencies.put(entry.getKey(), entry.getValue());
        }

        for (Class<?> dependantConfigClass : dependantConfigClasses) {
            if (!visited.contains(dependantConfigClass)) {
                configClassDependencies(dependantConfigClass, aggregator, visited);
            }
        }
        return aggregator;
    }

    public static void detectCycles(Class<?> rootConfigClass) {
        if (rootConfigClass.isAnnotationPresent(DependsOn.class)) {
            // If config is spread across multiple config classes, check for cycles across files
            DependencyAggregator aggregator = configClassDependencies(rootConfigClass, DependencyAggregator.create(), new HashSet<>());
            checkForCycles(aggregator.configClassDependencies);
            checkForCycles(aggregator.instanceClassDependencies);
        } else {
            Map<Class<?>, Class<?>[]> dependencies = getDependenciesForProviderMethods(rootConfigClass);
            checkForCycles(dependencies);
        }
    }

    private static Map<Class<?>, Class<?>[]> getDependenciesForProviderMethods(Class<?> configClass) {
        var providerMethods = Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Provides.class))
                .toList();
        Map<Class<?>, Class<?>[]> dependencies = new HashMap<>();
        for (Method method : providerMethods) {
            if (dependencies.containsKey(method.getReturnType()))
                throw new IllegalStateException("Only 1 provider per type allowed");
            dependencies.put(method.getReturnType(), method.getParameterTypes());
        }
        return dependencies;
    }

    private static void checkForCycles(Map<Class<?>, Class<?>[]> dependencies) {
        CycleResult result = hasCycles(dependencies);
        // TODO: 2023-07-08 Replace with pattern matching for switch once released
        if (result instanceof DetectedCycle cycle)
            throw new CyclicDependencyException(dependencyCycleString(cycle.cyclePath));
    }

    private static CycleResult hasCycles(Map<Class<?>, Class<?>[]> dependencies) {
        var visited = new HashSet<Class<?>>();
        for (Class<?> dependency : dependencies.keySet()) {
            if (!visited.contains(dependency)) {
                CycleResult result = hasCycle(dependency, dependencies, visited, new LinkedHashSet<>());
                if (result instanceof DetectedCycle cycle)
                    return cycle;
            }
        }
        return new NoCycle();
    }

    private static CycleResult hasCycle(Class<?> clazz,
                                        Map<Class<?>, Class<?>[]> dependencies,
                                        Set<Class<?>> visited,
                                        LinkedHashSet<Class<?>> path) {
        visited.add(clazz);
        path.add(clazz);
        Class<?>[] dependencyList = dependencies.get(clazz);
        if (dependencyList == null)
            throw new IllegalStateException("Dependencies null for " + clazz);
        for (Class<?> dependency : dependencyList) {
            if (!visited.contains(dependency)) {
                CycleResult result = hasCycle(dependency, dependencies, visited, path);
                if (result instanceof DetectedCycle cycle)
                    return cycle;
            } else if (path.contains(dependency)) {
                return new DetectedCycle(toCycleList(path, dependency));
            }
        }
        path.remove(clazz);
        return new NoCycle();
    }

    private static List<String> toCycleList(LinkedHashSet<Class<?>> path, Class<?> tail) {
        var cycle = new ArrayList<>(path);
        cycle.add(tail);
        return cycle.stream().map(Class::getSimpleName).toList();
    }

    private static String dependencyCycleString(List<String> cyclePath) {
        return String.join(" -> ", cyclePath);
    }

    sealed interface CycleResult permits DetectedCycle, NoCycle {
    }

    record DetectedCycle(List<String> cyclePath) implements CycleResult {
    }

    record NoCycle() implements CycleResult {
    }

    record DependencyAggregator(Map<Class<?>, Class<?>[]> configClassDependencies,
                                Map<Class<?>, Class<?>[]> instanceClassDependencies) {
        static DependencyAggregator create() {
            return new DependencyAggregator(new HashMap<>(), new HashMap<>());
        }
    }
}
