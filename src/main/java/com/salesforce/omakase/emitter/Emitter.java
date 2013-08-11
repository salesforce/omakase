/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.*;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.reflect.TypeToken;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class Emitter {
    private static final AnnotationScanner scanner = new AnnotationScanner();

    /** cache of class -> (class + supers). Only supers marked as {@link Subscribable} are stored */
    private static final LoadingCache<Class<?>, Set<Class<?>>> cache = CacheBuilder.newBuilder()
        .weakKeys() // use weak references for keys (classes)
        .initialCapacity(32) // expected number of subscribable syntax classes
        .build(new CacheLoader<Class<?>, Set<Class<?>>>() {
            @Override
            public Set<Class<?>> load(Class<?> klass) {
                final Builder<Class<?>> builder = ImmutableSet.<Class<?>>builder();
                for (Class<?> type : TypeToken.of(klass).getTypes().rawTypes()) {
                    if (type.isAnnotationPresent(Subscribable.class)) {
                        builder.add(type);
                    }
                }
                return builder.build();
            }
        });

    /** order is important so that subscribers are notified in the order they were registered */
    private final Multimap<Class<?>, Subscription> subscriptions = LinkedHashMultimap.create(32, 8);

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
        checkNotNull(event, "event cannot be null");

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
