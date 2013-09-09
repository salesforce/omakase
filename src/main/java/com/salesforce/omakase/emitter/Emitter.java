/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.Plugin;

import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TESTME
 * <p/>
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
                final Builder<Class<?>> builder = ImmutableSet.builder();
                for (Class<?> type : TypeToken.of(klass).getTypes().rawTypes()) {
                    if (type.isAnnotationPresent(Subscribable.class)) {
                        builder.add(type);
                    }
                }
                return builder.build();
            }
        });

    /** order is important so that subscribers are notified in the order they were registered, hence linked multimap */
    private final Multimap<Class<?>, Subscription> preprocessors = LinkedHashMultimap.create(8, 2);
    private final Multimap<Class<?>, Subscription> processors = LinkedHashMultimap.create(32, 4);
    private final Multimap<Class<?>, Subscription> validators = LinkedHashMultimap.create(32, 4);

    private SubscriptionPhase phase = SubscriptionPhase.PREPROCESS;

    /**
     * Sets the current {@link SubscriptionPhase}. This determines which registered subscribers receive broadcasts.
     *
     * @param phase
     *     The current phase.
     */
    public void phase(SubscriptionPhase phase) {
        this.phase = checkNotNull(phase, "phase cannot be null");
    }

    /**
     * Gets the current {@link SubscriptionPhase}.
     *
     * @return The current {@link SubscriptionPhase}.
     */
    public SubscriptionPhase phase() {
        return phase;
    }

    /**
     * Registers an instance of an object to receive broadcasted events (usually a {@link Plugin} instance).
     * <p/>
     * The methods on the class of the object will be scanned for applicable annotations (e.g., {@link Rework}, {@link Validate}).
     * The methods will be invoked when the matching event is broadcasted in the applicable phase.
     *
     * @param subscriber
     *     Register this object to receive events.
     */
    public void register(Object subscriber) {
        for (Entry<Class<?>, Subscription> entry : scanner.scan(subscriber).entries()) {
            Subscription subscription = entry.getValue();

            // perf -- segment the subscriber to the correct phase bucket
            switch (subscription.phase()) {
            case PREPROCESS:
                preprocessors.put(entry.getKey(), subscription);
                break;
            case PROCESS:
                processors.put(entry.getKey(), subscription);
                break;
            case VALIDATE:
                validators.put(entry.getKey(), subscription);
                break;
            }
        }
    }

    /**
     * Sends an event to registered subscribers of the given event type (or any type within the given event type's parent
     * hierarchy).
     * <p/>
     * "event" here usually refers to an instance of a {@link Syntax} unit (but this class is built generically to emit anything
     * really).
     *
     * @param event
     *     The event instance. "event" here usually is an instance of an object (e.g., one of the {@link Syntax} objects).
     * @param em
     *     The {@link ErrorManager} instance.
     */
    public void emit(Object event, ErrorManager em) {
        switch (phase) {
        case PREPROCESS:
            emit(preprocessors, event, em);
            break;
        case PROCESS:
            emit(processors, event, em);
            break;
        case VALIDATE:
            emit(validators, event, em);
            break;
        }
    }

    /** handles emits for a particular phase */
    private void emit(Multimap<Class<?>, Subscription> multimap, Object event, ErrorManager em) {
        Class<? extends Object> eventType = event.getClass();

        // for each subscribable type in the event's hierarchy, inform each subscription to that type
        for (Class<?> klass : hierarchy(eventType)) {

            // perf -- skip the multimap check if it doesn't contain the key
            if (!multimap.containsKey(klass)) continue;

            for (Subscription subscription : multimap.get(klass)) {
                subscription.deliver(event, em);
            }
        }
    }

    private Set<Class<?>> hierarchy(Class<?> klass) {
        return cache.getUnchecked(klass);
    }
}
