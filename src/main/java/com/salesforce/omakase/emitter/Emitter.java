/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import java.util.Set;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class Emitter {
    private static final AnnotationScanner scanner = new AnnotationScanner();

    private static final LoadingCache<Class<?>, Set<Class<?>>> cache = CacheBuilder.newBuilder()
        .weakKeys()
        .build(new CacheLoader<Class<?>, Set<Class<?>>>() {
            @Override
            @SuppressWarnings({ "unchecked", "rawtypes" })
            public Set<Class<?>> load(Class<?> klass) {
                return (Set)TypeToken.of(klass).getTypes().rawTypes();
            }
        });

    /** order is important so that subscribers are notified in the order they were registered */
    private final Multimap<Class<?>, Subscription> subscriptions = LinkedHashMultimap.create();

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
     * @param type
     *            TODO
     * @param event
     *            TODO
     */
    public void emit(SubscriptionType type, Object event) {
        for (Class<?> klass : hierarchy(event.getClass())) {
            for (Subscription subscription : subscriptions.get(klass)) {
                subscription.inform(type, event);
            }
        }
    }

    private Set<Class<?>> hierarchy(Class<?> klass) {
        return cache.getUnchecked(klass);
    }
}
