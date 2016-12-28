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

package com.salesforce.omakase.ast.collection;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.annotation.Rework;

import java.util.Optional;

/**
 * Represents an item that appears in a group or chain of other related units, for usage with {@link SyntaxCollection}.
 * <p>
 * Note that uniqueness within the same {@link SyntaxCollection} is enforced, which means that if you prepend or append an
 * instance that already exists in the {@link SyntaxCollection} it will be moved. This also means that if you prepend or append an
 * instance to a difference {@link SyntaxCollection} it will be moved out of the original collection as well. If this is not what
 * you want then try looking at {@link Syntax#copy()}.
 * <p>
 * To remove a unit from the syntax tree, use {@link #destroy()}. A destroyed unit cannot be placed back in the tree (among other
 * reasons because it may have bypassed necessary subscription methods), however it can still be copied.
 *
 * @param <P>
 *     Type of the (P)arent object containing this collection (e.g., {@link SelectorPart}s have {@link Selector}s as the parent).
 * @param <T>
 *     The (T)ype of units to be grouped with.
 *
 * @author nmcwilliams
 * @see SyntaxCollection
 */
public interface Groupable<P, T extends Groupable<P, T>> extends Syntax {
    /**
     * Gets whether this unit is the first within its group.
     * <p>
     * Please note, if you are making decisions based on this value then keep in mind that any rework plugins may add or remove
     * new units before or after this one. As such, this usually means it's best that plugins with {@link Rework} methods
     * utilizing this value are registered last.
     *
     * @return True if the unit is first within its group. Always returns true if this unit is detached.
     */
    boolean isFirst();

    /**
     * Gets whether this unit is the last within its group.
     * <p>
     * Please note, if you are making decisions based on this value then keep in mind that any rework plugins may add or remove
     * new units before or after this one. As such, this usually means it's best that plugins with {@link Rework} methods
     * utilizing this value are registered last.
     *
     * @return True if the unit is last within its group. Always returns true if this unit is detached.
     */
    boolean isLast();

    /**
     * Gets the unit following this one in the same collection, if there is one. This will always return an empty optional if this
     * unit has not been added to any collection.
     *
     * @return The next unit, or an empty {@link Optional} if there isn't one.
     */
    Optional<T> next();

    /**
     * Gets the unit preceding this one in the same collection, if there is one. This will always return an empty optional if this
     * unit has not been added to any collection.
     *
     * @return The previous unit, or an empty {@link Optional} if there isn't one.
     */
    Optional<T> previous();

    /**
     * Prepends the given unit before this one.
     * <p>
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
     * <p>
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
     * Replaces this unit with the given one.
     * <p>
     * Specifically, it prepends the unit to this one, then calls {@link #destroy()} on this unit. Note that {@link #destroy()}
     * basically makes this unit worthless, so do not do anything with it afterwards (like trying to put it back in the tree).
     *
     * @param unit
     *     Replace with this unit.
     *
     * @return this, for chaining.
     */
    Groupable<P, T> replaceWith(T unit);

    /**
     * Severs the connection between this unit and its collection. Generally this method is used internally. If you want to remove
     * a unit from the syntax tree, use {@link #destroy()} instead.
     *
     * @return this, for chaining.
     */
    Groupable<P, T> unlink();

    /**
     * Removes this unit from its collection. Use this method to remove a unit from the syntax tree.
     * <p>
     * Once removed, the unit cannot be re-added to any collection, however it can still be copied. Destroyed units will no longer
     * be broadcasted to any subsequent plugins.
     */
    void destroy();

    /**
     * Whether the unit has been destroyed.
     *
     * @return True if the unit has been destroyed.
     */
    boolean isDestroyed();

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
     * <p>
     * This can be used to find all peers of the unit (e.g., all other declarations in the same rule as this one).
     *
     * @return The group {@link SyntaxCollection}. If working with this term before it has been properly linked then this may
     * return null. This is not the case for normal subscription methods.
     */
    SyntaxCollection<P, T> group();

    /**
     * Gets the parent {@link Syntax} unit that owns the {@link SyntaxCollection} that contains this unit. See {@link
     * SyntaxCollection#parent()}.
     *
     * @return The parent. If working with this term before it has been properly linked then this may return null. This is not the
     * case for normal subscription methods.
     */
    P parent();
}
