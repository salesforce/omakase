/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.SubscriptionType;

/**
 * A {@link Broadcaster} that stores each event for later querying and retrieval. It can also optionally relay each
 * event to another {@link Broadcaster}.
 * 
 * <p>
 * Note that currently only {@link SubscriptionType#CREATED} events are stored.
 * 
 * @author nmcwilliams
 */
public class CollectingBroadcaster implements Broadcaster {
    private final List<? super Syntax> collected = Lists.newArrayList();
    private final Broadcaster relay;

    /**
     * Constructs a new {@link CollectingBroadcaster} instance that will <em>not</em> relay any events to another
     * {@link Broadcaster}.
     */
    public CollectingBroadcaster() {
        this(null);
    }

    /**
     * Constructs a new {@link CollectingBroadcaster} instance that will relay all events to the given
     * {@link Broadcaster}.
     * 
     * @param relay
     *            Relay all events to this {@link Broadcaster}.
     */
    public CollectingBroadcaster(Broadcaster relay) {
        this.relay = relay;
    }

    @Override
    public <T extends Syntax> void broadcast(SubscriptionType type, T syntax) {
        if (type == SubscriptionType.CREATED) {
            collected.add(syntax);
        }
        if (relay != null) {
            relay.broadcast(type, syntax);
        }
    }

    /**
     * Gets all broadcasted events of type {@link SubscriptionType#CREATED} that are instances of the given class.
     * 
     * @param <T>
     *            Type of the {@link Syntax} unit.
     * @param klass
     *            Filter {@link Syntax} units that are instances of this class.
     * @return All matching {@link Syntax} units that are instances of the given class.
     */
    public <T extends Syntax> Iterable<T> filter(Class<T> klass) {
        return Iterables.filter(collected, klass);
    }

    /**
     * Finds the first {@link Syntax} unit that is an instance of the given class.
     * 
     * @param <T>
     *            Type of the {@link Syntax} unit.
     * @param klass
     *            Get the first {@link Syntax} unit that is an instance of this class.
     * @return The single matching {@link Syntax} unit that is an instance of the given class, or
     *         {@link Optional#absent()} if not present.
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> find(Class<T> klass) {
        // Predicates.instanceOf ensures that this is a safe cast
        return (Optional<T>)Iterables.tryFind(collected, Predicates.instanceOf(klass));
    }
}
