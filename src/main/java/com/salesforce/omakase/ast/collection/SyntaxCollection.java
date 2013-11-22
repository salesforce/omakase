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
     * Gets whether this collection is empty, or all contained elements are detached.
     *
     * @return True if there are no units in this collection or all units are detached.
     *
     * @see Groupable#isDetached()
     */
    boolean isEmptyOrAllDetached();

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
     * Gets the next unit after the given one, if there is one.
     *
     * @param unit
     *     Get the next unit after this one.
     *
     * @return The next unit, or {@link Optional#absent()} if not present.
     */
    Optional<T> next(T unit);

    /**
     * Gets the previous unit before the given one, if there is one.
     *
     * @param unit
     *     Get the unit before this one.
     *
     * @return The previous unit, or {@link Optional#absent()} if not present.
     */
    Optional<T> previous(T unit);

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
     * duplicated (by reference), not moved. If you would like to move the unit (e.g., remove from current position then prepend)
     * then use {@link #moveBefore} instead.
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
     * Moves the specified unit before the given index unit.
     * <p/>
     * If the specified unit already exists in this collection it will first be removed and then prepended. If it does not
     * currently exist in this collection it will just be prepended.
     * <p/>
     * The index unit must exist in this collection or an exception will be thrown. If you would like to duplicate the unit
     * instead of moving it, use #prependBefore() instead.
     *
     * @param index
     *     Prepend before this unit.
     * @param unit
     *     Move this unit.
     *
     * @return this, for chaining.
     *
     * @throws IllegalArgumentException
     *     If the index unit does not exist in this collection.
     */
    SyntaxCollection<P, T> moveBefore(T index, T unit) throws IllegalArgumentException;

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
     * duplicated (by reference), not moved. If you would like to move the unit (e.g., remove from current position then append)
     * then use #moveAfter instead.
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
     * Moves the specified unit after the given index unit.
     * <p/>
     * If the specified unit already exists in this collection it will first be removed and then appended. If it does not
     * currently exist in this collection it will just be appended.
     * <p/>
     * The index unit must exist in this collection or an exception will be thrown. If you would like to duplicate the unit
     * instead of moving it, use #appendAfter instead.
     *
     * @param index
     *     Append after this unit.
     * @param unit
     *     Append this unit.
     *
     * @return this, for chaining.
     *
     * @throws IllegalArgumentException
     *     If the index unit does not exist in this collection.
     */
    SyntaxCollection<P, T> moveAfter(T index, T unit) throws IllegalArgumentException;

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
     * Calls {@link Syntax#propagateBroadcast(Broadcaster)} on all units within this collection using the given {@link
     * Broadcaster}.
     *
     * @param broadcaster
     *     Propagate using this {@link Broadcaster}.
     */
    void propagateBroadcast(Broadcaster broadcaster);
}
