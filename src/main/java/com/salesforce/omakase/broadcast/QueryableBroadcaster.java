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

package com.salesforce.omakase.broadcast;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.Status;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * A {@link Broadcaster} that stores each event for later querying and retrieval.
 *
 * @author nmcwilliams
 */
public final class QueryableBroadcaster extends AbstractBroadcaster {
    /** important to maintain broadcast order */
    private final List<Broadcastable> collected = new ArrayList<>();

    /**
     * Constructs a new {@link QueryableBroadcaster} instance that will <em>not</em> relay any events to another {@link
     * Broadcaster}.
     */
    public QueryableBroadcaster() {
        this(null);
    }

    /**
     * Constructs a new {@link QueryableBroadcaster} instance that will relay all events to the given {@link Broadcaster}.
     *
     * @param relay
     *     Wrap (decorate) this broadcaster. All broadcasts will be relayed to this one.
     */
    public QueryableBroadcaster(Broadcaster relay) {
        wrap(relay);
    }

    @Override
    public void broadcast(Broadcastable broadcastable) {
        collected.add(broadcastable);

        // update status to prevent a unit from being broadcasted too many times
        if (broadcastable.status() == Status.UNBROADCASTED) {
            broadcastable.status(Status.QUEUED);
        }

        if (relay != null) {
            relay.broadcast(broadcastable);
        }
    }

    /**
     * Retrieves all broadcasted events.
     *
     * @return All broadcasted events.
     */
    public Iterable<Broadcastable> all() {
        return Iterables.unmodifiableIterable(collected);
    }

    /**
     * Gets all broadcasted events that are instances of the given class.
     *
     * @param <T>
     *     Type of the {@link Broadcastable} unit.
     * @param klass
     *     Filter {@link Broadcastable} units that are instances of this class.
     *
     * @return All matching {@link Broadcastable} units that are instances of the given class.
     */
    public <T extends Broadcastable> Iterable<T> filter(Class<T> klass) {
        return Iterables.filter(collected, klass);
    }

    /**
     * Finds the first {@link Broadcastable} unit that is an instance of the given class.
     *
     * @param <T>
     *     Type of the {@link Broadcastable} unit.
     * @param klass
     *     Get the first {@link Broadcastable} unit that is an instance of this class.
     *
     * @return The first matching {@link Broadcastable} unit that is an instance of the given class, or {@link Optional#absent()}
     *         if not present.
     */
    @SuppressWarnings("unchecked")
    public <T extends Broadcastable> Optional<T> find(Class<T> klass) {
        // Predicates.instanceOf ensures that this is a safe cast
        return (Optional<T>)Iterables.tryFind(collected, Predicates.instanceOf(klass));
    }

    /**
     * Similar to {@link #find(Class)}, except that this verifies at most one broadcasted event to have occurred.
     *
     * @param <T>
     *     Type of the {@link Broadcastable} unit.
     * @param klass
     *     Get the one and only {@link Broadcastable} unit that is an instance of this class.
     *
     * @return The single matching {@link Broadcastable} unit that is an instance of the given class, or {@link Optional#absent()}
     *         if not present.
     */
    public <T extends Broadcastable> Optional<T> findOnly(Class<T> klass) {
        Optional<T> found = find(klass);
        if (found.isPresent()) {
            checkState(collected.size() == 1, "expected to find only one broadcasted event");
        }
        return found;
    }
}
