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

package com.salesforce.omakase.ast.collection;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

/**
 * A collection of related {@link Syntax} units.
 * <p/>
 * If you are using any of these methods in a plugin you will need to register the {@link SyntaxTree} as a dependency. See {@link
 * DependentPlugin} for more details.
 *
 * @param <T>
 *     The type of {@link Syntax} contained within the collection.
 * @param <P>
 *     Type of the parent object containing this collection (e.g., {@link SelectorPart}s have {@link Selector}s as the parent).
 *
 * @author nmcwilliams
 */
public interface SyntaxCollection<P, T extends Syntax & Groupable<P, T>> extends Iterable<T> {
    /**
     * Gets the number of units in the collection.
     *
     * @return Size of this collection.
     */
    int size();

    /**
     * Gets whether this collection contains any units.
     *
     * @return True if there are no units in this collection.
     */
    boolean isEmpty();

    /**
     * Gets whether this collection is empty, or all contained elements are detached.
     *
     * @return True if there are no units in this collection or all units are detached.
     *
     * @see Groupable#isDetached()
     */
    boolean isEmptyOrAllDetached();

    /**
     * Gets whether the given unit is contained within this collection.
     *
     * @param unit
     *     Check if this unit is contained within this collection.
     *
     * @return True if the unit is contained within this collection.
     */
    boolean contains(T unit);

    /**
     * Gets the first unit in the collection.
     *
     * @return The first unit in the collection, or {@link Optional#absent()} if empty.
     */
    Optional<T> first();

    /**
     * Gets the last unit in the collection.
     *
     * @return The last unit in the collection, or {@link Optional#absent()} if empty.
     */
    Optional<T> last();

    /**
     * Prepends the given unit the beginning of this collection.
     *
     * @param unit
     *     The unit to prepend.
     *
     * @return this, for chaining.
     */
    SyntaxCollection<P, T> prepend(T unit);

    /**
     * Prepends all of the given units to the beginning of this collection.
     *
     * @param units
     *     The units to add.
     *
     * @return this, for chaining.
     */
    SyntaxCollection<P, T> prependAll(Iterable<T> units);

    /**
     * Prepends the given unit before the given existing unit.
     *
     * @param existing
     *     The unit to prepend.
     * @param unit
     *     Prepend this unit before the existing unit.
     *
     * @return this, for chaining.
     *
     * @throws IllegalArgumentException
     *     If existing is not contained within this collection.
     */
    SyntaxCollection<P, T> prependBefore(T existing, T unit) throws IllegalArgumentException;

    /**
     * Appends the given unit to the end of this collection.
     *
     * @param unit
     *     The unit to append.
     *
     * @return this, for chaining.
     */
    SyntaxCollection<P, T> append(T unit);

    /**
     * Appends all of the given units to the end of this collection.
     *
     * @param units
     *     The units to append.
     *
     * @return this, for chaining.
     */
    SyntaxCollection<P, T> appendAll(Iterable<T> units);

    /**
     * Appends the given unit after the given existing unit.
     *
     * @param existing
     *     The unit that already exists in this collection.
     * @param unit
     *     The unit to append.
     *
     * @return this, for chaining.
     *
     * @throws IllegalArgumentException
     *     If existing is not contained within this collection.
     */
    SyntaxCollection<P, T> appendAfter(T existing, T unit) throws IllegalArgumentException;

    /**
     * Replaces <b>all</b> existing units with the given units.
     *
     * @param units
     *     Replace all existing (if any) units with these.
     *
     * @return this, for chaining.
     */
    SyntaxCollection<P, T> replaceExistingWith(Iterable<T> units);

    /**
     * Removes a unit from this collection. If this collection does not contain the given unit an exception will be thrown. It's
     * preferable to call {@link Groupable#detach()} over this.
     *
     * @param unit
     *     The unit to remove.
     *
     * @return this, for chaining.
     *
     * @throws IllegalArgumentException
     *     if the unit is not contained within this collection.
     */
    SyntaxCollection<P, T> detach(T unit);

    /**
     * Detaches <b>all</b> units from this collection.
     *
     * @return The detached units.
     */
    Iterable<T> clear();

    /**
     * Gets the parent {@link Syntax} unit that owns this collection.
     *
     * @return The parent.
     */
    P parent();

    /**
     * Specifies the {@link Broadcaster} to use when new units are added to the collection.
     *
     * @param broadcaster
     *     Used to broadcast newly added units.
     *
     * @return this, for chaining.
     */
    SyntaxCollection<P, T> broadcaster(Broadcaster broadcaster);

    /**
     * Calls {@link Syntax#propagateBroadcast(Broadcaster)} on all units within this collection using the given {@link
     * Broadcaster}.
     *
     * @param broadcaster
     *     Propagate using this {@link Broadcaster}.
     */
    void propagateBroadcast(Broadcaster broadcaster);
}
