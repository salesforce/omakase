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

package com.salesforce.omakase.util;

import com.salesforce.omakase.ast.collection.Groupable;
import com.salesforce.omakase.ast.collection.SyntaxCollection;

/**
 * Collection of common {@link Action}s.
 *
 * @author nmcwilliams
 */
public final class Actions {
    private Actions() {}

    /**
     * Gets an {@link Action} that will call {@link Groupable#destroy()} on all instances.
     *
     * @return The {@link Action} instance.
     */
    public static Action<Groupable<?, ?>> destroy() {
        return DESTROY;
    }

    @SuppressWarnings("Convert2Lambda")
    private static final Action<Groupable<?, ?>> DESTROY = new Action<Groupable<?, ?>>() {
        @Override
        public void apply(Iterable<? extends Groupable<?, ?>> instances) {
            for (Groupable<?, ?> instance : instances) instance.destroy();
        }
    };

    /**
     * Gets a {@link ActionWithSubject} that will move a collection of instances before a given subject.
     *
     * @param <T>
     *     The type of the instances.
     *
     * @return The {@link ActionWithSubject} instance.
     */
    public static <T extends Groupable<?, T>> ActionWithSubject<T> moveBefore() {
        return new MoveBefore<>();
    }

    private static class MoveBefore<T extends Groupable<?, T>> implements ActionWithSubject<T> {
        @Override
        public void apply(T subject, Iterable<? extends T> instances) {
            SyntaxCollection<?, T> collection = subject.group();
            for (T instance : instances) {
                collection.prependBefore(subject, instance);
            }
        }
    }

    /**
     * Gets a {@link ActionWithSubject} that will move a collection of instances after a given subject.
     *
     * @param <T>
     *     The type of the instances.
     *
     * @return The {@link ActionWithSubject} instance.
     */
    public static <T extends Groupable<?, T>> ActionWithSubject<T> moveAfter() {
        return new MoveAfter<>();
    }

    private static class MoveAfter<T extends Groupable<?, T>> implements ActionWithSubject<T> {
        @Override
        public void apply(T subject, Iterable<? extends T> instances) {
            SyntaxCollection<?, T> collection = subject.group();
            T index = subject;
            for (T instance : instances) {
                collection.appendAfter(index, instance);
                index = instance; // maintain order
            }
        }
    }
}
