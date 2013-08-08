/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import java.lang.reflect.Method;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class AnnotationScanner {
    private static final LoadingCache<Class<?>, ImmutableList<Method>> cache =
            CacheBuilder.newBuilder()
                .weakKeys()
                .build(new CacheLoader<Class<?>, ImmutableList<Method>>() {
                    @Override
                    public ImmutableList<Method> load(Class<?> concreteClass) throws Exception {
                        return findAll(concreteClass);
                    }
                });

    public Multimap<Class<?>, Subscription> scan(Object subscriber) {
        Multimap<Class<?>, Subscription> subscriptions = HashMultimap.create();

        for (Method method : getAll(subscriber.getClass())) {
            Class<?> type = method.getParameterTypes()[0];
            subscriptions.put(type, new Subscription(subscriber, method));
        }
        return subscriptions;
    }

    private static ImmutableList<Method> getAll(Class<?> klass) {
        return cache.getUnchecked(klass);
    }

    private static ImmutableList<Method> findAll(Class<?> klass) {
        ImmutableList.Builder<Method> result = ImmutableList.builder();
        for (Method method : klass.getMethods()) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) { throw new IllegalArgumentException("Method " + method
                        + " has @Subscribe annotation, but requires " + parameterTypes.length
                        + " arguments.  Event handler methods must require a single argument."); }
                Class<?> eventType = parameterTypes[0];
                result.add(method);
                break;
            }
        }
        return result.build();
    }
}
