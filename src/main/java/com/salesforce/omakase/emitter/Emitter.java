/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import java.util.Set;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class Emitter {
    private static final AnnotationScanner scanner = new AnnotationScanner();

    private static final LoadingCache<Class<?>, Set<Class<?>>> cache =
            CacheBuilder.newBuilder()
                .weakKeys()
                .build(new CacheLoader<Class<?>, Set<Class<?>>>() {
                    @Override
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    public Set<Class<?>> load(Class<?> klass) {
                        return (Set)TypeToken.of(klass).getTypes().rawTypes();
                    }
                });

    private final SetMultimap<Class<?>, Subscription> subscriptions = HashMultimap.create();

    /**
     * TODO Description
     * 
     * @param subscriber
     *            TODO
     */
    public void register(Object subscriber) {
        subscriptions.putAll(scanner.scan(subscriber));
    }

    /**
     * TODO Description
     * 
     * @param event
     *            TODO
     */
    public void emit(Object event) {
        for (Class<?> type : flattened(event.getClass())) {
            for (Subscription subscription : subscriptions.get(type)) {
                subscription.deliver(event);
            }
        }
    }

    private Set<Class<?>> flattened(Class<?> klass) {
        return cache.getUnchecked(klass);
    }
}
