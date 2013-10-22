/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.broadcast.emitter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.reflect.TypeToken;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.Plugin;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Responsible for sending an event (object) to registered listeners.
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

    /* order is important so that subscribers are notified in the order they were registered, hence linked map */
    private final Map<Class<?>, Set<Subscription>> processors = new LinkedHashMap<Class<?>, Set<Subscription>>(32);
    private final Map<Class<?>, Set<Subscription>> validators = new LinkedHashMap<Class<?>, Set<Subscription>>(32);

    private SubscriptionPhase phase = SubscriptionPhase.PROCESS;

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
            Set<Subscription> subscriptions;

            switch (subscription.phase()) {
            case PROCESS:
                subscriptions = processors.get(entry.getKey());
                if (subscriptions == null) {
                    subscriptions = new HashSet<Subscription>();
                    processors.put(entry.getKey(), subscriptions);
                }
                subscriptions.add(subscription);
                break;
            case VALIDATE:
                subscriptions = validators.get(entry.getKey());
                if (subscriptions == null) {
                    subscriptions = new HashSet<Subscription>();
                    validators.put(entry.getKey(), subscriptions);
                }
                subscriptions.add(subscription);
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
        if (phase == SubscriptionPhase.PROCESS) {
            emit(processors, event, em);
        } else {
            emit(validators, event, em);
        }
    }

    /** handles emits for a particular phase */
    private void emit(Map<Class<?>, Set<Subscription>> map, Object event, ErrorManager em) {
        Class<?> eventType = event.getClass();

        // for each subscribable type in the event's hierarchy, inform each subscription to that type
        for (Class<?> klass : hierarchy(eventType)) {
            Set<Subscription> subscriptions = map.get(klass);
            if (subscriptions == null) continue;

            for (Subscription subscription : subscriptions) {
                subscription.deliver(event, em);
            }
        }
    }

    private Set<Class<?>> hierarchy(Class<?> klass) {
        return cache.getUnchecked(klass);
    }
}
