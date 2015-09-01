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
import com.salesforce.omakase.writer.Writable;

/**
 * A collection of related {@link Syntax} units.
 *
 * @param <P>
 *     Type of the (P)arent object containing this collection (e.g., {@link SelectorPart}s have {@link Selector}s as the parent).
 * @param <T>
 *     The (T)ype of units to be grouped with.
 *
 * @author nmcwilliams
 */
public interface SyntaxCollection<P, T extends Groupable<P, T>> extends Iterable<T> {
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
     * Gets whether this collection is empty, or all contained elements are not writable.
     *
     * @return True if there are no units in this collection or all units are not writable.
     *
     * @see Writable#isWritable()
     */
    boolean isEmptyOrNoneWritable();

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
     * Gets the next unit after the given one, if there is one.
     *
     * @param unit
     *     Get the next unit after this one.
     *
     * @return The next unit, or {@link Optional#absent()} if not present.
     *
     * @throws IllegalArgumentException
     *     if the given unit is not contained within this collection.
     */
    Optional<T> next(T unit);

    /**
     * Gets the previous unit before the given one, if there is one.
     *
     * @param unit
     *     Get the unit before this one.
     *
     * @return The previous unit, or {@link Optional#absent()} if not present.
     *
     * @throws IllegalArgumentException
     *     if the given unit is not contained within this collection.
     */
    Optional<T> previous(T unit);

    /**
     * Finds the <em>first</em> item in this collection that is a type of a the given class.
     *
     * @param klass
     *     Find the first instance of this class.
     * @param <S>
     *     Type of the instance to find.
     *
     * @return The first instance, or {@link Optional#absent()} if none match.
     */
    <S extends T> Optional<S> find(Class<S> klass);

    /**
     * Prepends the specified unit to the beginning of this collection.
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
     * Prepends the specified unit before the given index unit.
     * <p/>
     * The index unit must be present within this collection. If the unit to prepend already exists in this collection it will be
     * moved.
     *
     * @param index
     *     Prepend before this unit.
     * @param unit
     *     Prepend this unit.
     *
     * @return this, for chaining.
     *
     * @throws IllegalArgumentException
     *     If the index unit is not contained within this collection.
     */
    SyntaxCollection<P, T> prependBefore(T index, T unit) throws IllegalArgumentException;

    /**
     * Appends the specified unit to the end of this collection.
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
     * Appends the specified unit after the given index unit.
     * <p/>
     * The index unit must be present within this collection. If the unit to append already exists in this collection it will be
     * moved.
     *
     * @param index
     *     Append after this unit.
     * @param unit
     *     Append this unit.
     *
     * @return this, for chaining.
     *
     * @throws IllegalArgumentException
     *     If existing is not contained within this collection.
     */
    SyntaxCollection<P, T> appendAfter(T index, T unit) throws IllegalArgumentException;

    /**
     * Removes a unit from this collection.  It's preferable to call {@link Groupable#destroy()} over this.
     *
     * @param unit
     *     The unit to remove.
     *
     * @return this, for chaining.
     */
    SyntaxCollection<P, T> remove(T unit);

    /**
     * Removes <b>all</b> units from this collection.
     *
     * @return this, for chaining.
     */
    SyntaxCollection<P, T> clear();

    /**
     * Replaces <b>all</b> existing units with the given unit.
     *
     * @param unit
     *     Replace all existing (if any) units with this one.
     *
     * @return this, for chaining.
     */
    SyntaxCollection<P, T> replaceExistingWith(T unit);

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
     * Gets the parent {@link Syntax} unit that owns this collection.
     *
     * @return The parent. If working with a collection before it's owner is properly linked into the tree then this may return
     * null.
     */
    P parent();

    /**
     * Calls {@link Syntax#propagateBroadcast(Broadcaster)} on all units within this collection using the given {@link
     * Broadcaster}.
     *
     * @param broadcaster
     *     Propagate using this {@link Broadcaster}.
     */
    void propagateBroadcast(Broadcaster broadcaster);
}
