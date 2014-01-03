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
import com.salesforce.omakase.broadcast.annotation.Rework;

/**
 * Represents an item that appears in a group or chain of other related units, for usage with {@link SyntaxCollection}.
 * <p/>
 * Note that uniqueness within the same {@link SyntaxCollection} is enforced, which means that if you prepend or append an
 * instance that already exists in the {@link SyntaxCollection} it will be moved. This also means that if you prepend or append an
 * instance to a difference {@link SyntaxCollection} it will be moved out of the original collection as well. If this is not what
 * you want then try looking at {@link Syntax#copy()}.
 * <p/>
 * To remove a unit from the syntax tree, use {@link #destroy()}. A destroyed unit cannot be placed back in the tree, however it
 * can still be copied. (It cannot be added for multiple reasons, including the fact that destroying a unit short-circuits
 * broadcasting it, which means some validators on it might not have had a chance to run).
 *
 * @param <P>
 *     Type of the (P)arent object containing this collection (e.g., {@link SelectorPart}s have {@link Selector}s as the parent).
 * @param <T>
 *     The (T)ype of units to be grouped with.
 *
 * @author nmcwilliams
 * @see SyntaxCollection
 */
public interface Groupable<P, T extends Groupable<P, T>> extends Syntax<T> {
    /**
     * Gets whether this unit is the first within its group.
     * <p/>
     * Please note, if you are making decisions based on this value then keep in mind that any rework plugins may add or remove
     * new units before or after this one. As such, this usually means it's best that plugins with {@link Rework} methods
     * utilizing this value are registered last.
     *
     * @return True if the unit is first within its group. Always returns true if this unit is detached.
     */
    boolean isFirst();

    /**
     * Gets whether this unit is the last within its group.
     * <p/>
     * Please note, if you are making decisions based on this value then keep in mind that any rework plugins may add or remove
     * new units before or after this one. As such, this usually means it's best that plugins with {@link Rework} methods
     * utilizing this value are registered last.
     *
     * @return True if the unit is last within its group. Always returns true if this unit is detached.
     */
    boolean isLast();

    /**
     * Gets the unit following this one in the same collection, if there is one. This will always return {@link Optional#absent
     * ()} if this unit has not been added to any collection.
     *
     * @return The next unit, or {@link Optional#absent()} if there isn't one.
     */
    Optional<T> next();

    /**
     * Gets the unit preceding this one in the same collection, if there is one. This will always return {@link Optional#absent
     * ()} if this unit has not been added to any collection.
     *
     * @return The previous unit, or {@link Optional#absent()} if there isn't one.
     */
    Optional<T> previous();

    /**
     * Prepends the given unit before this one.
     * <p/>
     * Note that uniqueness within the same {@link SyntaxCollection} is enforced, which means that if you prepend or append an
     * instance that already exists in the {@link SyntaxCollection} it will be moved. This also means that if you prepend or
     * append an instance to a difference {@link SyntaxCollection} it will be moved out of the original collection as well. If
     * this is not what you want then try looking at {@link Syntax#copy()}.
     *
     * @param unit
     *     The unit to prepend.
     *
     * @return this, for chaining.
     *
     * @throws IllegalStateException
     *     If this unit is currently detached (doesn't belong to any collection) or it has been destroyed.
     */
    Groupable<P, T> prepend(T unit);

    /**
     * Appends the given unit after this one.
     * <p/>
     * Note that uniqueness within the same {@link SyntaxCollection} is enforced, which means that if you prepend or append an
     * instance that already exists in the {@link SyntaxCollection} it will be moved. This also means that if you prepend or
     * append an instance to a difference {@link SyntaxCollection} it will be moved out of the original collection as well. If
     * this is not what you want then try looking at {@link Syntax#copy()}.
     *
     * @param unit
     *     The unit to append.
     *
     * @return this, for chaining.
     *
     * @throws IllegalStateException
     *     If this unit is currently detached (doesn't belong to any group) or it has been destroyed.
     */
    Groupable<P, T> append(T unit);

    /**
     * Severes the connection between this unit and its collection. Generally this method is used internally. If you want to
     * remove a unit from the syntax tree, use {@link #destroy()} instead.
     *
     * @return this, for chaining.
     */
    Groupable<P, T> unlink();

    /**
     * Removes this unit from its collection. Use this method to remove a unit from the syntax tree.
     * <p/>
     * Once removed, the unit cannot be re-added to any collection, however it can still be copied. Destroyed units will no longer
     * be broadcasted to any subsequent plugins.
     */
    void destroy();

    /**
     * Whether the unit has been destroyed.
     *
     * @return True if the unit has been destroyed.
     */
    boolean destroyed();

    /**
     * Sets the group. Internal method only! Do not call directly or behavior will be unexpected.
     *
     * @param group
     *     The group group.
     *
     * @return this, for chaining.
     */
    Groupable<P, T> group(SyntaxCollection<P, T> group);

    /**
     * Gets the group {@link SyntaxCollection} of this unit.
     * <p/>
     * This can be used to find all peers of the unit (e.g., all other declarations in the same rule as this one).
     *
     * @return The group {@link SyntaxCollection}, or {@link Optional#absent()} if the group is not specified.
     */
    Optional<SyntaxCollection<P, T>> group();

    /**
     * Gets the parent {@link Syntax} unit that owns the {@link SyntaxCollection} that contains this unit. See {@link
     * SyntaxCollection#parent()}.
     *
     * @return The parent, or {@link Optional#absent()} if this unit has not been added to any collection.
     */
    Optional<P> parent();
}
