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
import com.salesforce.omakase.ast.Status;

/**
 * A {@link Broadcaster} that is interested in only a single broadcast of a particular type.
 * <p/>
 * Any broadcasts that do not match the expected type will be ignored, and only the <em>first</em> broadcast of the desired type
 * will be stored. All broadcasts, matching or not, will still be passed along to the relay if the relay is specified.
 * <p/>
 * This is a better performing {@link Broadcaster} over {@link QueryableBroadcaster} as it does not create a new list object. To
 * further this benefit, use the {@link #reset()} as well.
 *
 * @param <T>
 *     The expected broadcastable type.
 *
 * @author nmcwilliams
 */
public final class SingleInterestBroadcaster<T extends Broadcastable> extends AbstractBroadcaster {
    private final Class<T> klass;
    private T broadcasted;

    /**
     * Constructs a new {@link SingleInterestBroadcaster} instance that will <em>not</em> relay any events to another {@link
     * Broadcaster}.
     *
     * @param klass
     *     Class of the expected broadcastable.
     */
    public SingleInterestBroadcaster(Class<T> klass) {
        this(klass, null);
    }

    /**
     * Constructs a new {@link SingleInterestBroadcaster} instance that will relay all events to the given {@link Broadcaster}.
     *
     * @param klass
     *     Class of the expected broadcastable.
     * @param relay
     *     Wrap (decorate) this broadcaster. All broadcasts will be relayed to this one.
     */
    public SingleInterestBroadcaster(Class<T> klass, Broadcaster relay) {
        this.klass = klass;
        wrap(relay);
    }

    @Override
    public void broadcast(Broadcastable broadcastable) {
        if (broadcastable.status() == Status.UNBROADCASTED) {
            broadcastable.status(Status.QUEUED);
        }

        // store off the first occurrence of our expected type
        if (broadcasted == null && klass.isInstance(broadcastable)) {
            broadcasted = klass.cast(broadcastable);
        }

        if (relay != null) {
            relay.broadcast(broadcastable);
        }
    }

    /**
     * Gets the single broadcasted event.
     *
     * @return The broadcasted event, or {@link Optional#absent()} if nothing was broadcasted.
     */
    public Optional<T> broadcasted() {
        return Optional.fromNullable(broadcasted);
    }

    /**
     * Resets the current broadcasted event to null (allows for reuse of this same object).
     *
     * @return this, for chaining.
     */
    public SingleInterestBroadcaster<T> reset() {
        this.broadcasted = null;
        return this;
    }

    /**
     * Convenience method to create a new {@link SingleInterestBroadcaster} instance.
     *
     * @param klass
     *     Class of the expected broadcastable.
     * @param <E>
     *     The expected broadcastable type.
     *
     * @return The new instance.
     */
    public static <E extends Broadcastable> SingleInterestBroadcaster<E> of(Class<E> klass) {
        return new SingleInterestBroadcaster<>(klass);
    }

    /**
     * @param klass
     *     Class of the expected broadcastable.
     * @param relay
     *     Wrap (decorate) this broadcaster. All broadcasts will be relayed to this one.
     * @param <E>
     *     The expected broadcastable type.
     *
     * @return The new instance.
     */
    public static <E extends Broadcastable> SingleInterestBroadcaster<E> of(Class<E> klass, Broadcaster relay) {
        return new SingleInterestBroadcaster<>(klass, relay);
    }
}
