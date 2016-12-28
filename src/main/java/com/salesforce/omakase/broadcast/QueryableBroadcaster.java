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

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A broadcaster that stores all received broadcasts for later retrieval.
 * <p>
 * This can handle finding broadcasts of all types, but prefer to use one of the {@link TypeInterestBroadcaster}s if you're
 * only interested in a single type.
 *
 * @author nmcwilliams
 */
public final class QueryableBroadcaster extends AbstractBroadcaster {
    /** important to maintain broadcast order */
    private final List<Broadcastable> collected = new ArrayList<>();

    /**
     * Creates a new {@link QueryableBroadcaster}.
     */
    public QueryableBroadcaster() {}

    /**
     * Creates a new {@link QueryableBroadcaster} and calls {@link #chain(Broadcaster)} on this instance, passing in the
     * given {@link Broadcaster}.
     *
     * @param broadcaster
     *     Add this broadcaster to the end of the chain.
     */
    public QueryableBroadcaster(Broadcaster broadcaster) {
        chain(broadcaster);
    }

    @Override
    public void broadcast(Broadcastable broadcastable) {
        collected.add(broadcastable);
        relay(broadcastable);
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
        return collected.stream().filter(klass::isInstance).map(klass::cast).collect(Collectors.toList());
    }

    /**
     * Finds the first {@link Broadcastable} unit that is an instance of the given class.
     *
     * @param <T>
     *     Type of the {@link Broadcastable} unit.
     * @param klass
     *     Get the first {@link Broadcastable} unit that is an instance of this class.
     *
     * @return The first matching {@link Broadcastable} unit that is an instance of the given class.
     */
    public <T extends Broadcastable> Optional<T> find(Class<T> klass) {
        return collected.stream().filter(klass::isInstance).map(klass::cast).findFirst();
    }

    /**
     * Returns true if any events were received by this broadcaster.
     *
     * @return true if any events were received.
     */
    public boolean hasAny() {
        return !collected.isEmpty();
    }

    /**
     * Retrieves all broadcasted events.
     *
     * @return All broadcasted events.
     */
    public List<Broadcastable> all() {
        return ImmutableList.copyOf(collected);
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
