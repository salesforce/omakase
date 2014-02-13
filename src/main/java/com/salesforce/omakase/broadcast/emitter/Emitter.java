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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.broadcast.Broadcastable;
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
import java.util.TreeSet;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Responsible for sending an event ({@link Broadcastable}) to registered listeners.
 *
 * @author nmcwilliams
 */
public final class Emitter {
    private static final AnnotationScanner scanner = new AnnotationScanner();

    /** cache of class -> (class + supers). Only supers marked as {@link Subscribable} are stored */
    private static final Map<Class<?>, List<Class<?>>> hierarchyCache = new HashMap<Class<?>, List<Class<?>>>(32);

    /** subscriptions and validators (direct, specific per type). Segmented separately for perf */
    private final Map<Class<?>, Set<Subscription>> processors = new HashMap<Class<?>, Set<Subscription>>(32);
    private final Map<Class<?>, Set<Subscription>> validators = new HashMap<Class<?>, Set<Subscription>>(32);

    /**
     * subscriptions and validators (direct and indirect, i.e., hierarchy, this is important for ordering).
     * <p/>
     * For example,
     * <p/>
     * <pre><code>
     * // Class1 registered first, then Class2
     * processors.get(SimpleSelector.class) -> Class1#Subscription(SimpleSelector)
     * processors.get(ClassSelector.class) -> Class2#Subscription(ClassSelector)
     * processorsCache.get(ClassSelector.cache) -> Class1#Subscription(SimpleSelector), Class2#Subscription(ClassSelector)
     * processorsCache.get(SimpleSelector.cache) -> Class2#Subscription(ClassSelector)
     * </code></pre>
     * <p/>
     * ClassSelector is the concrete instance. Thus the event given will have getClass == ClassSelector, and thus when the
     * hierarchy is looked at for the event, ClassSelector will come before SimpleSelector. However since Class1 is registered
     * first, its subscription to SimpleSelector must be invoked before Class2's subscription to ClassSelector.
     */
    private final Map<Class<?>, Iterable<Subscription>> processorsCache = new HashMap<Class<?>, Iterable<Subscription>>();
    private final Map<Class<?>, Iterable<Subscription>> validatorsCache = Maps.newHashMap();

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
     * Sends an event to registered subscribers of the given event type (i.e., class), including any subscribers to types within
     * the event's class hierarchy.
     * <p/>
     * "Event" here refers to an instance of a {@link Broadcastable}.
     *
     * @param event
     *     The event instance.
     * @param em
     *     The {@link ErrorManager} instance.
     */
    public void emit(Broadcastable event, ErrorManager em) {
        // for each subscribable type in the event's hierarchy, inform each subscription to that type
        for (Subscription subscription : subscriptions(event)) {
            if (event.status() == Status.NEVER_EMIT) return;
            subscription.deliver(event, em);
        }
    }

    /** gets all subscriptions (including hierarchy) for the given event's class (see notes above for more details). */
    private Iterable<Subscription> subscriptions(Broadcastable event) {
        Map<Class<?>, Iterable<Subscription>> cache = (phase == SubscriptionPhase.PROCESS) ? processorsCache : validatorsCache;
        Iterable<Subscription> subscriptions = cache.get(event.getClass());

        if (subscriptions == null) {
            Map<Class<?>, Set<Subscription>> map = (phase == SubscriptionPhase.PROCESS) ? processors : validators;
            TreeSet<Subscription> tree = Sets.newTreeSet(); // tree set important for maintaining plugin registration order

            for (Class<?> klass : hierarchy(event.getClass())) {
                Set<Subscription> matching = map.get(klass);
                if (matching == null || matching.isEmpty()) continue;

                for (Subscription subscription : matching) {
                    tree.add(subscription);
                }
            }
            subscriptions = Lists.newArrayList(tree);
            cache.put(event.getClass(), subscriptions);
        }
        return subscriptions;
    }

    /** gets class -> (class + supers) */
    private List<Class<?>> hierarchy(Class<?> klass) {
        List<Class<?>> hierarchy = hierarchyCache.get(klass);

        if (hierarchy == null) {
            if (!klass.isAnnotationPresent(Subscribable.class)) {
                hierarchy = ImmutableList.of();
            } else {
                ImmutableList.Builder<Class<?>> builder = ImmutableList.builder();
                for (Class<?> type : TypeToken.of(klass).getTypes().rawTypes()) {
                    if (type.isAnnotationPresent(Subscribable.class)) {
                        builder.add(type);
                    }
                }
                hierarchy = builder.build();
            }

            hierarchyCache.put(klass, hierarchy);
        }

        return hierarchy;
    }
}
