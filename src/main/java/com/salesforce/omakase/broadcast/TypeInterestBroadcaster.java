/*
 * Copyright (c) 2017, salesforce.com, inc.
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

import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An {@link InterestBroadcaster} that is interested in all broadcasts of a particular type.
 * <p>
 * This is similar to {@link QueryableBroadcaster} except this only collects instances a single type, and as such should be
 * preferred when possible.
 *
 * @param <T>
 *     The expected broadcastable type.
 *
 * @author nmcwilliams
 */
public final class TypeInterestBroadcaster<T> extends AbstractBroadcaster implements InterestBroadcaster<T> {
    private final Class<T> klass;
    private final List<T> list = new ArrayList<>();

    /**
     * Creates a new {@link TypeInterestBroadcaster}.
     *
     * @param klass
     *     Class of the expected broadcastable.
     */
    public TypeInterestBroadcaster(Class<T> klass) {
        this.klass = checkNotNull(klass, "klass cannot be null");
    }

    @Override
    public void broadcast(Broadcastable broadcastable) {
        if (klass.isInstance(broadcastable)) {
            list.add(klass.cast(broadcastable));
        }

        relay(broadcastable);
    }

    @Override
    public Optional<T> one() {
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Iterable<T> gather() {
        return Iterables.unmodifiableIterable(list);
    }

    @Override
    public InterestBroadcaster<T> reset() {
        list.clear();
        return this;
    }

    /**
     * Convenience method to create a new {@link TypeInterestBroadcaster} instance.
     *
     * @param klass
     *     Class of the expected broadcastable.
     * @param <T>
     *     The expected broadcastable type.
     *
     * @return The new instance.
     */
    public static <T extends Broadcastable> TypeInterestBroadcaster<T> of(Class<T> klass) {
        return new TypeInterestBroadcaster<>(klass);
    }
}
