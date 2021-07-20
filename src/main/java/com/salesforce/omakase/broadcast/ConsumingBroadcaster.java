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

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A broadcaster that will forward broadcasted units of a certain type to a
 * {@link Consumer}.
 *
 * @param <T>
 *     The type of units to consume.
 *
 * @author nmcwilliams
 */
public final class ConsumingBroadcaster<T extends Broadcastable> extends AbstractBroadcaster {
    private final Consumer<T> consumer;
    private final Class<T> klass;
    private final Function<T, Boolean> customComparableLogic;

    /**
     * Creates a new broadcaster that matches units of the given class.
     *
     * @param klass
     *     The type of unit to consume.
     * @param consumer
     *     The consumer.
     * @param customComparableLogic
     *      Additional logic function if return true then the {@code consumer}
     *      will be called. If false, then it will not be called. This function
     *      is used in conjunction with the {@code klass} attribute.
     * @see #ConsumingBroadcaster(Class, Consumer)
     */
    public ConsumingBroadcaster(Class<T> klass, Consumer<T> consumer, Function<T, Boolean> customComparableLogic) {
        this.klass = klass;
        this.consumer = consumer;
        this.customComparableLogic = customComparableLogic;
    }
    
    /**
     * Creates a new broadcaster that matches units of the given class.
     *
     * @param klass
     *     The type of unit to consume.
     * @param consumer
     *     The consumer.
     * @see #ConsumingBroadcaster(Class, Consumer, Function)
     */
    public ConsumingBroadcaster(Class<T> klass, Consumer<T> consumer) {
        this(klass, consumer, null);
    }

    @Override
    public void broadcast(Broadcastable broadcastable) {
        if (klass.isInstance(broadcastable)) {
            final T input = klass.cast(broadcastable);
            if ((customComparableLogic == null) || customComparableLogic.apply(input)) {
                consumer.accept(input);
            }
        }
        relay(broadcastable);
    }
}
