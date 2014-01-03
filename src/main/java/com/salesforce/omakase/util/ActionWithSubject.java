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

/**
 * An action that can be applied to instances of specified type with respect to a subject instance.
 *
 * @param <T>
 *     Type of objects this {@link Action} works on.
 *
 * @author nmcwilliams
 */
public interface ActionWithSubject<T> {
    /**
     * Apply this action for the given subject on the given instances.
     *
     * @param subject
     *     The main subject of the action.
     * @param instances
     *     Apply the action on these instances.
     */
    void apply(T subject, Iterable<? extends T> instances);
}
