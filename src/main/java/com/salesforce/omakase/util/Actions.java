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

package com.salesforce.omakase.util;

import com.salesforce.omakase.ast.collection.Groupable;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Collection of common {@link Action}s.
 *
 * @author nmcwilliams
 */
public final class Actions {
    private Actions() {}

    /**
     * Gets an {@link Action} that will call {@link Groupable#detach()} on all instances.
     *
     * @return The {@link Action} instance.
     */
    public static Action<Groupable<?, ?>> detach() {
        return DETACH;
    }

    private static final Action<Groupable<?, ?>> DETACH = new Action<Groupable<?, ?>>() {
        @Override
        public void apply(Iterable<? extends Groupable<?, ?>> instances) {
            for (Groupable<?, ?> instance : instances) instance.detach();
        }
    };

    /**
     * TESTME
     * <p/>
     * Gets a {@link SubjectAction} that will move a collection of instances before a given subject.
     *
     * @param <T>
     *     The type of the instances.
     *
     * @return The {@link SubjectAction} instance.
     */
    public static <T extends Groupable<?, T>> SubjectAction<T> moveBefore() {
        return new MoveBefore<T>();
    }

    private static class MoveBefore<T extends Groupable<?, T>> implements SubjectAction<T> {
        @Override
        public void apply(T subject, Iterable<T> instances) {
            for (T instance : instances) {
                checkArgument(!subject.isDetached(), "subject cannot be detached");
                subject.group().get().moveBefore(subject, instance);
            }
        }
    }

    /**
     * TESTME
     * <p/>
     * Gets a {@link SubjectAction} that will move a collection of instances after a given subject.
     *
     * @param <T>
     *     The type of the instances.
     *
     * @return The {@link SubjectAction} instance.
     */
    public static <T extends Groupable<?, T>> SubjectAction<T> moveAfter() {
        return new MoveAfter<T>();
    }

    private static class MoveAfter<T extends Groupable<?, T>> implements SubjectAction<T> {
        @Override
        public void apply(T subject, Iterable<T> instances) {
            for (T instance : instances) {
                checkArgument(!subject.isDetached(), "subject cannot be detached");
                subject.group().get().moveAfter(subject, instance);
            }
        }
    }
}
