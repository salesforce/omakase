/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import java.lang.reflect.Method;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.*;
import com.salesforce.omakase.As;
import com.salesforce.omakase.plugin.Plugin;

/**
 * Scans for annotated methods on classes.
 * 
 * @author nmcwilliams
 */
final class AnnotationScanner {
    private static final String ONE_ARG = "Methods annotated with @Subscribe must have exactly one argument: '%s'";

    /** cache of which methods on a {@link Plugin} are {@link Subscription} methods */
    private static final LoadingCache<Class<?>, Set<SubscriptionMetadata>> cache = CacheBuilder.newBuilder()
        .weakKeys()
        .build(new CacheLoader<Class<?>, Set<SubscriptionMetadata>>() {
            @Override
            public Set<SubscriptionMetadata> load(Class<?> klass) throws Exception {
                return parse(klass);
            }
        });

    /**
     * Create {@link Subscription} objects for each subscribed event on the given class.
     * 
     * @param subscriber
     *            The class with the subscription methods.
     * @return A multimap of syntax object (event) to subscription object.
     */
    public Multimap<Class<?>, Subscription> scan(Object subscriber) {
        // linked multimap because insertion order is important
        Multimap<Class<?>, Subscription> subscriptions = LinkedHashMultimap.create();

        for (SubscriptionMetadata md : cache.getUnchecked(subscriber.getClass())) {
            Class<?> event = md.method.getParameterTypes()[0];
            Subscription s = new Subscription(md.type, subscriber, md.method, md.filter);
            subscriptions.put(event, s);
        }
        return subscriptions;
    }

    private static Set<SubscriptionMetadata> parse(Class<?> klass) {
        // using tree set to maintain a predictable order.
        Set<SubscriptionMetadata> set = Sets.newTreeSet();

        for (Method method : klass.getMethods()) {

            if (method.isAnnotationPresent(Rework.class)) {
                Rework subscribe = method.getAnnotation(Rework.class);
                Class<?>[] parameterTypes = method.getParameterTypes();
                SubscriptionMetadata metadata = new SubscriptionMetadata();

                // must have exactly one argument
                if (parameterTypes.length != 1) { throw new SubscriptionException(String.format(ONE_ARG, method)); }

                metadata.method = method;
                metadata.type = subscribe.type();

                set.add(metadata);
            }
        }

        return set;
    }

    /**
     * Metadata parsed from a method annotated with {@link Rework}. Note that this implements {@link Comparable} to
     * allow subscription methods within the same class to be explicitly or predictably ordered.
     */
    private static final class SubscriptionMetadata implements Comparable<SubscriptionMetadata> {
        Method method;
        SubscriptionType type;

        @Override
        public int compareTo(SubscriptionMetadata o) {
            return ComparisonChain.start()
                .compare(type, o.type)
                .compare(method, o.method, Ordering.arbitrary())
                .result();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(type, method);
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof SubscriptionMetadata) {
                SubscriptionMetadata that = (SubscriptionMetadata)object;
                return Objects.equal(type, that.type) && Objects.equal(method, that.method);
            }
            return false;
        }

        @Override
        public String toString() {
            return As.string(this)
                .add("type", type)
                .add("method", method.getName())
                .toString();
        }
    }
}
