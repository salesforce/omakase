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
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class CollectingBroadcaster implements Broadcaster {
    private final List<? super Syntax> collected = Lists.newArrayList();
    private final Broadcaster relay;

    /**
     * TODO
     */
    public CollectingBroadcaster() {
        this(null);
    }

    /**
     * TODO
     * 
     * @param relay
     *            TODO
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
     * TODO Description
     * 
     * @param <T>
     *            TODO
     * @param klass
     *            TODO
     * @return TODO
     */
    public <T extends Syntax> Iterable<T> filter(Class<T> klass) {
        return Iterables.filter(collected, klass);
    }

    /**
     * TODO Description
     * 
     * @param <T>
     *            TODO
     * @param klass
     *            TODO
     * @return TODO
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> find(Class<T> klass) {
        // Predicates.instanceOf ensures that this is a safe cast
        return (Optional<T>)Iterables.tryFind(collected, Predicates.instanceOf(klass));
    }

}
