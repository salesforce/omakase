/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import java.lang.reflect.Method;
import java.util.Set;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.Plugin;

/**
 * Scans for annotated methods on classes.
 * 
 * @author nmcwilliams
 */
final class AnnotationScanner {
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
     * Creates subscription objects for each subscribed event on the class of the given instance.
     * 
     * @param subscriber
     *            The class with the subscription methods.
     * @return A multimap of syntax object (event) to subscription object.
     */
    public Multimap<Class<?>, Subscription> scan(Object subscriber) {
        // linked multimap because we need to maintain insertion order
        Multimap<Class<?>, Subscription> subscriptions = LinkedHashMultimap.create();

        for (SubscriptionMetadata md : cache.getUnchecked(subscriber.getClass())) {
            subscriptions.put(md.event, new Subscription(md.phase, subscriber, md.method));
        }

        return subscriptions;
    }

    private static Set<SubscriptionMetadata> parse(Class<?> klass) {
        Set<SubscriptionMetadata> set = Sets.newHashSet();

        for (Method method : klass.getMethods()) {
            boolean annotated = false;

            // the preprocess annotation
            if (method.isAnnotationPresent(PreProcess.class)) {
                annotated = true;

                // must have exactly one parameter
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1) throw new SubscriptionException(Message.ONE_PARAM, method);

                // add the metadata
                set.add(new SubscriptionMetadata(method, params[0], SubscriptionPhase.PREPROCESS));
            }

            // the observe annotation
            if (method.isAnnotationPresent(Observe.class)) {
                if (annotated == true) throw new SubscriptionException(Message.ANNOTATION_EXCLUSIVE, method);
                annotated = true;

                // must have exactly one parameter
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1) throw new SubscriptionException(Message.ONE_PARAM, method);

                // add the metadata
                set.add(new SubscriptionMetadata(method, params[0], SubscriptionPhase.PROCESS));
            }

            // the rework annotation
            if (method.isAnnotationPresent(Rework.class)) {
                if (annotated == true) throw new SubscriptionException(Message.ANNOTATION_EXCLUSIVE, method);
                annotated = true;

                // must have exactly one parameter
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1) throw new SubscriptionException(Message.ONE_PARAM, method);

                // add the metadata
                set.add(new SubscriptionMetadata(method, params[0], SubscriptionPhase.PROCESS));
            }

            // the validate annotation
            if (method.isAnnotationPresent(Validate.class)) {
                if (annotated == true) throw new SubscriptionException(Message.ANNOTATION_EXCLUSIVE, method);
                annotated = true;

                // must have exactly two parameters
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 2) throw new SubscriptionException(Message.TWO_PARAMS, method);

                // second param must be an error manager
                boolean errorManager = ErrorManager.class.isAssignableFrom(params[1]);
                if (!errorManager) throw new SubscriptionException(Message.MISSING_ERROR_MANAGER, method);

                // add the metadata
                set.add(new SubscriptionMetadata(method, params[0], SubscriptionPhase.VALIDATE));
            }
        }

        return set;
    }

    /** data object */
    private static final class SubscriptionMetadata {
        final Method method;
        final Class<?> event;
        final SubscriptionPhase phase;

        public SubscriptionMetadata(Method method, Class<?> event, SubscriptionPhase phase) {
            this.method = method;
            this.event = event;
            this.phase = phase;
        }
    }
}
