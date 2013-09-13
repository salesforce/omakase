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

import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.emitter.PreProcess;
import com.salesforce.omakase.emitter.Rework;
import com.salesforce.omakase.emitter.Validate;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

/**
 * Represents an item that appears in a group or chain of other related units, for usage with {@link SyntaxCollection}.
 * <p/>
 * If you are using any of these methods in a plugin you will need to register the {@link SyntaxTree} as a dependency. See {@link
 * DependentPlugin} for more details.
 * <p/>
 * In many cases you may need to check if this item is <em>detached</em> first (true if explicitly detached or if it's a new
 * instance not yet added to the tree). Detached items usually should be ignored, except to reattach.
 *
 * @param <T>
 *     The type of units to be grouped with.
 * @param <P>
 *     Type of the parent object containing this collection (e.g., {@link SelectorPart}s have {@link Selector}s as the parent).
 *
 * @author nmcwilliams
 * @see SyntaxCollection
 */
public interface Groupable<P, T extends Syntax & Groupable<P, T>> extends Syntax {

    /**
     * Gets whether this unit is the first within its group.
     * <p/>
     * Some units will not be linked if the {@link SyntaxTree} plugin is not enabled. For example, {@link Rule}, {@link Selector},
     * {@link Declaration} (and if unlinked this will always return false).
     * <p/>
     * Please note, if you are making decisions based on this value there are a few things to keep in mind. First, if you are
     * doing something in a {@link PreProcess} method, there is a good chance there are still more units to be added, so while
     * this unit may be first or last now that could shortly change. Secondly, any rework plugins may add or remove new units
     * before or after this one. As such, don't use this in a {@link PreProcess} method, be thoughtful about usage in a {@link
     * Rework} method, and prefer if possible to use in a {@link Validate} method, when all preprocessing and rework should be
     * completed.
     *
     * @return True if the unit is first within its group. Always returns false if this unit is detached.
     */
    boolean isFirst();

    /**
     * Gets whether this unit is the last within its group.
     * <p/>
     * Some units will not be linked if the {@link SyntaxTree} plugin is not enabled. For example, {@link Rule}, {@link Selector},
     * {@link Declaration} (and if unlinked this will always return false).
     * <p/>
     * Please note, if you are making decisions based on this value there are a few things to keep in mind. First, if you are
     * doing something in a {@link PreProcess} method, there is a good chance there are still more units to be added, so while
     * this unit may be first or last now that could shortly change. Secondly, any rework plugins may add or remove new units
     * before or after this one. As such, don't use this in a {@link PreProcess} method, be thoughtful about usage in a {@link
     * Rework} method, and prefer if possible to use in a {@link Validate} method, when all preprocessing and rework should be
     * completed.
     *
     * @return True if the unit is last within its group. Always returns false if this unit is detached.
     */
    boolean isLast();

    /**
     * Prepends the given unit before this one.
     *
     * @param unit
     *     The unit to prepend.
     *
     * @return this, for chaining.
     *
     * @throws IllegalStateException
     *     If this unit is currently detached (doesn't belong to any group).
     */
    Groupable<P, T> prepend(T unit);

    /**
     * Appends the given unit after this one.
     *
     * @param unit
     *     The unit to append.
     *
     * @return this, for chaining.
     *
     * @throws IllegalStateException
     *     If this unit is currently detached (doesn't belong to any group).
     */
    Groupable<P, T> append(T unit);

    /** Detaches (removes) this unit from the group {@link SyntaxCollection}. */
    void detach();

    /**
     * Gets whether this unit is detached. This should be true if either this unit was explicitly detached, or it is yet to be
     * added to a unit within the tree.
     *
     * @return True if this unit is detached.
     */
    boolean isDetached();

    /**
     * Sets the group group. This should only be called internally... calling it yourself may result in expected behavior.
     *
     * @param group
     *     The group group.
     *
     * @return this, for chaining.
     */
    Groupable<P, T> group(SyntaxCollection<P, T> group);

    /**
     * Gets the group {@link SyntaxCollection} of this unit.
     *
     * @return The group {@link SyntaxCollection}.
     *
     * @throws IllegalStateException
     *     If this unit is currently detached (doesn't belong to any group).
     */
    SyntaxCollection<P, T> group();

    /**
     * Gets the parent {@link Syntax} unit that owns the {@link SyntaxCollection} that contains this unit. See {@link
     * SyntaxCollection#parent()}.
     *
     * @return The parent, or null if currently detached.
     */
    P parent();
}
