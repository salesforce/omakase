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
import com.salesforce.omakase.Message;

/**
 * A {@link Broadcaster} that expects at most one particular {@link Broadcastable} instance to be broadcasted.
 * <p/>
 * If more than one or the wrong type of unit is broadcasted then an exception is thrown.
 *
 * @param <T>
 *     The expected broadcastable type.
 *
 * @author nmcwilliams
 */
public class SingleBroadcaster<T extends Broadcastable> extends AbstractBroadcaster {
    private final Class<T> klass;
    private T broadcasted;

    /**
     * Constructs a new {@link SingleBroadcaster} instance that will <em>not</em> relay any events to another {@link
     * Broadcaster}.
     *
     * @param klass
     *     Class of the expected broadcastable.
     */
    public SingleBroadcaster(Class<T> klass) {
        this(klass, null);
    }

    /**
     * Constructs a new {@link SingleBroadcaster} instance that will relay all events to the given {@link Broadcaster}.
     *
     * @param klass
     *     Class of the expected broadcastable.
     * @param relay
     *     Wrap (decorate) this broadcaster. All broadcasts will be relayed to this one.
     */
    public SingleBroadcaster(Class<T> klass, Broadcaster relay) {
        this.klass = klass;
        wrap(relay);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void broadcast(Broadcastable broadcastable) {
        if (broadcasted != null) throw new IllegalArgumentException(Message.ONE_BROADCASTED_EVENT.message());
        if (!klass.isInstance(broadcastable)) throw new IllegalArgumentException(Message.WRONG_INSTANCE.message(klass));

        // cast is safe -- guarded by above isInstance check
        broadcasted = (T)broadcastable;

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
    public SingleBroadcaster<T> reset() {
        this.broadcasted = null;
        return this;
    }
}
