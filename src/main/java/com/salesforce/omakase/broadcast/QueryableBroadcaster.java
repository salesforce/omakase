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

package com.salesforce.omakase.broadcast;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.Status;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * A {@link Broadcaster} that stores each event for later querying and retrieval.
 *
 * @author nmcwilliams
 */
public final class QueryableBroadcaster extends AbstractBroadcaster {
    /** important to maintain broadcast order */
    private final List<Broadcastable> collected = new ArrayList<>(32);

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
     * <p>
     * If using this in a loop, take note that performing a refine action on the filtered object may result in a {@link
     * ConcurrentModificationException}, as the refinement may result in the broadcast of additional syntax units. In this case
     * you could make an immutable copy of the results first.
     *
     * @return All broadcasted events.
     */
    public Iterable<Broadcastable> all() {
        return Iterables.unmodifiableIterable(collected);
    }

    /**
     * Gets all broadcasted events that are instances of the given class.
     * <p>
     * If using this in a loop, take note that performing a refine action on the filtered object may result in a {@link
     * ConcurrentModificationException}, as the refinement may result in the broadcast of additional syntax units. In this case
     * you could make an immutable copy of the results first.
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
     * if not present.
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
     * if not present.
     */
    public <T extends Broadcastable> Optional<T> findOnly(Class<T> klass) {
        Optional<T> found = find(klass);
        if (found.isPresent()) {
            checkState(collected.size() == 1, "expected to find only one broadcasted event");
        }
        return found;
    }

    /**
     * Gets the total number of broadcasted units.
     *
     * @return The number of broadcasted units.
     */
    public int count() {
        return collected.size();
    }
}
