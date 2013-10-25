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

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.Plugin;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
    private static final Map<Class<?>, List<Class<?>>> map = new HashMap<Class<?>, List<Class<?>>>(32);

    private final Map<Class<?>, Set<Subscription>> processors = new HashMap<Class<?>, Set<Subscription>>(32);
    private final Map<Class<?>, Set<Subscription>> validators = new HashMap<Class<?>, Set<Subscription>>(32);

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

            // using LinkedHashSets below to main registration order
            if (subscription.phase() == SubscriptionPhase.PROCESS) {
                subscriptions = processors.get(entry.getKey());
                if (subscriptions == null) {
                    subscriptions = new LinkedHashSet<Subscription>(5);
                    processors.put(entry.getKey(), subscriptions);
                }
                subscriptions.add(subscription);
            } else {
                subscriptions = validators.get(entry.getKey());
                if (subscriptions == null) {
                    subscriptions = new LinkedHashSet<Subscription>(5);
                    validators.put(entry.getKey(), subscriptions);
                }
                subscriptions.add(subscription);
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
            if (subscriptions == null || subscriptions.isEmpty()) continue;

            for (Subscription subscription : subscriptions) {
                subscription.deliver(event, em);
            }
        }
    }

    private List<Class<?>> hierarchy(Class<?> klass) {
        List<Class<?>> hierarchy = map.get(klass);

        if (hierarchy == null) {
            ImmutableList.Builder<Class<?>> builder = ImmutableList.builder();
            for (Class<?> type : TypeToken.of(klass).getTypes().rawTypes()) {
                if (type.isAnnotationPresent(Subscribable.class)) {
                    builder.add(type);
                }
            }
            hierarchy = builder.build();
            map.put(klass, hierarchy);
        }

        return hierarchy;
    }
}
