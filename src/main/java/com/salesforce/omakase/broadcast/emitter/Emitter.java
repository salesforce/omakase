/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.broadcast.emitter;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.parser.Grammar;

/**
 * Responsible for sending an event ({@link Broadcastable}) to registered listeners.
 *
 * @author nmcwilliams
 */
public final class Emitter {
    private static final AnnotationScanner scanner = new AnnotationScanner();

    /** Cache of class -> (class + supers). Only supers marked as {@link Subscribable} are stored. */
    private final Map<Class<?>, List<Class<?>>> hierarchyCache = new HashMap<>(32);

    /*
     * Map of (syntax) class (e.g., ClassSelector) to subscription Methods.
     *
     * This map only includes direct references (not supers). For example, get(ClassSelector.class) will not return
     * subscriptions to SimpleSelector. Those will be under the SimpleSelector.class entry.
     */
    private final Map<Class<?>, Set<Subscription>> directSubscriptions = new HashMap<>(16);

    /**
     * Map of (syntax) class to all applicable subscription Methods.
     * <p>
     * This map includes indirect (i.e., super classes / interfaces) of the syntax class, which is important for ordering:
     * <p>
     * <pre><code>
     * // Class1 registered first and has one subscription to SimpleSelector (which is a super of ClassSelector)
     * // Class2 registered second and has one subscription to ClassSelector
     * directSubscriptions.get(SimpleSelector.class) -> Class1#Subscription(SimpleSelector)
     * directSubscriptions.get(ClassSelector.class) -> Class2#Subscription(ClassSelector)
     * expandedSubscriptions.get(ClassSelector.class) -> Class1#Subscription(SimpleSelector), Class2#Subscription(ClassSelector)
     * </code></pre>
     * <p>
     * When a ClassSelector event is emitted we look at the hierarchy of this class and find that it includes ClassSelector and
     * SimpleSelector, in that order. However, since Class1 is registered first, its subscription to SimpleSelector must be
     * invoked before Class2's subscription to ClassSelector.
     */
    private final Map<Class<?>, Iterable<Subscription>> expandedSubscriptions = new HashMap<>(32);

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
     * <p>
     * The methods on the class of the object will be scanned for applicable annotations (e.g., {@link Rework}, {@link Validate}).
     * The methods will be invoked when the matching event is broadcasted in the applicable phase.
     *
     * @param subscriber
     *     Register this object to receive events.
     */
    public void register(Object subscriber) {
        for (Entry<Class<?>, Subscription> entry : scanner.scanSubscriptions(subscriber).entries()) {
            Set<Subscription> set = directSubscriptions.computeIfAbsent(entry.getKey(), k -> new LinkedHashSet<>(8));
            set.add(entry.getValue());
        }
    }

    /**
     * Sends an event to registered subscribers of the given event type (i.e., class), including any subscribers to types within
     * the event's class hierarchy.
     * <p>
     * "Event" here refers to an instance of a {@link Broadcastable}.
     *
     * @param event
     *     The event instance.
     * @param grammar
     *     The {@link Grammar} instance.
     * @param broadcaster
     *     The {@link Broadcaster} that refiners should use.
     * @param em
     *     The {@link ErrorManager} instance.
     */
    public void emit(Broadcastable event, Grammar grammar, Broadcaster broadcaster, ErrorManager em) {
        // for each subscribable type in the event's hierarchy, inform each subscription to that type
        for (Subscription subscription : subscriptions(event)) {
            if (subscription.phase() == phase) {
                // checking inside the loop because any subscription method can result in a change of status
                if (event.shouldBreakBroadcast(phase)) {
                    return; // break out when we no longer need to emit, e.g., for a destroyed unit or already refined
                }

                switch (phase) {
                case REFINE:
                    subscription.refine(event, grammar, broadcaster, em);
                    break;
                case PROCESS:
                    subscription.process(event, em);
                    break;
                case VALIDATE:
                    subscription.validate(event, em);
                    break;
                }
            }
        }
    }

    /**
     * Gets all subscriptions (including hierarchy) for the given event's class (irrespective of current phase, see
     * notes above for more details).
     */
    private Iterable<Subscription> subscriptions(Broadcastable event) {
        Iterable<Subscription> subscriptions = expandedSubscriptions.get(event.getClass());

        if (subscriptions == null) {
            Set<Subscription> tree = new TreeSet<>(); // tree set important for maintaining plugin registration order

            for (Class<?> klass : hierarchy(event.getClass())) {
                Set<Subscription> matching = directSubscriptions.get(klass);
                if (matching != null) {
                    tree.addAll(matching);
                }
            }
            subscriptions = ImmutableList.copyOf(tree);
            expandedSubscriptions.put(event.getClass(), subscriptions);
        }

        return subscriptions;
    }

    /** returns class -> (class + supers) */
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
