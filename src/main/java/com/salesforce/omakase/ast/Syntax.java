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

package com.salesforce.omakase.ast;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SimpleSelector;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.writer.Writable;

import java.util.List;

/**
 * A distinct unit of syntax within CSS.
 * <p/>
 * {@link Syntax} objects are used to represent the individual pieces of content of the parsed CSS source, and are the primary
 * objects used to construct the AST (Abstract Syntax Tree). Not all {@link Syntax} objects have content directly associated with
 * them. Some are used to represent the logical grouping of content, such as the {@link Rule}.
 * <p/>
 * Each unit has a particular line and column indicating where it was parsed within the source, except for dynamically created
 * units. You can check {@link #hasSourcePosition()} to see if a unit is dynamically created.
 * <p/>
 * It's important to remember that <em>unrefined</em> Syntax objects, unless validation is performed, may actually contain invalid
 * CSS. Simply refining the syntax unit will verify it's grammatical compliance, which can be coupled with custom validation to
 * ensure correct usage. See {@link Refinable} for more information.
 *
 * @param <C>
 *     (C)opiedType. Type of unit created when copied (usually the same type as the implementing class itself).
 *
 * @author nmcwilliams
 */
public interface Syntax<C> extends Writable, Broadcastable {
    /**
     * The line number within the source where this {@link Syntax} unit was parsed.
     *
     * @return The line number.
     */
    int line();

    /**
     * The column number within the source where this {@link Syntax} unit was parsed.
     *
     * @return The column number.
     */
    int column();

    /**
     * Gets whether this unit has a source location specified.
     * <p/>
     * This will be true for units within the original parsed source and false for dynamically created units.
     *
     * @return True if this unit has a source location specified.
     */
    boolean hasSourcePosition();

    /**
     * Performs a deep copy of the instance.
     * <p/>
     * This includes any inner syntax units, for example the selectors inside of a rule. This also carries over the comments and
     * orphaned comments.
     * <p/>
     * Keep in mind that copying is generally not preferred. Particularly, it is generally better to parse the source again than
     * to copy a {@link Stylesheet}. Copying a specific syntax unit may be appropriate when duplicating the terms in a declaration
     * or the selector parts in a selector.
     *
     * @return The new instance.
     */
    C copy();

    /**
     * Performs a deep copy of the instance.
     * <p/>
     * This includes any inner syntax units, for example the selectors inside of a rule. This also carries over the comments and
     * orphaned comments.
     * <p/>
     * If applicable and required by the supported browser versions (as specified in the given {@link SupportMatrix}), this will
     * also prefix certain values and members as part of the copy.
     * <p/>
     * Take the following for example:
     * <pre><code>
     * PropertyName pn = PropertyName.using("border-radius");
     * PropertyName copy = PropertyName.copyWithPrefix(Prefix.WEBKIT, support);
     * </code></pre>
     * <p/>
     * Assuming that a version of Chrome was added to the {@link SupportMatrix} that requires a prefix for the {@code
     * border-radius} property, the copy will have the webkit prefix, e.g., {@code -webkit-border-radius}.
     * <p/>
     * This should also cascade to any inner or child syntax units. For example, if calling on a {@link Declaration} instance,
     * both the property name and also any applicable parts of the declaration value should get prefixed.
     * <p/>
     * For implementations, it should be understood that both the prefix and support properties may be null.
     *
     * @param prefix
     *     Apply this {@link Prefix} is applicable.
     * @param support
     *     Represents the supported browser versions.
     *
     * @return The new instance.
     */
    C copy(Prefix prefix, SupportMatrix support);

    /**
     * Adds the given comments to this unit.
     * <p/>
     * Note that in the case of {@link Selector}s, it is preferred to add comments to the {@link Selector} object itself instead
     * of the individual {@link SimpleSelector}s inside of it. Likewise, it is preferred to add a comment to the {@link
     * Declaration} itself instead of the property name or value inside of it.
     *
     * @param comments
     *     The comments to add.
     *
     * @return this, for chaining.
     */
    Syntax<C> comments(List<String> comments);

    /**
     * Copies all comments from the given syntax unit.
     *
     * @param copyFrom
     *     Copy comments from this unit.
     *
     * @return this, for chaining.
     */
    Syntax<C> comments(Syntax<?> copyFrom);

    /**
     * Gets all comments <em>associated</em> with this {@link Syntax} unit.
     * <p/>
     * A unit is associated with all comments that directly precede it. In the case of selectors, both the {@link Selector} object
     * and the first {@link SimpleSelector} within the {@link Selector} object will return the same comments.
     *
     * @return The list of comments. Never returns null.
     */
    ImmutableList<Comment> comments();

    /**
     * Adds orphaned comments (comments that appears after or at the end of the unit).
     *
     * @param comments
     *     The comments to add.
     *
     * @return this, for chaining.
     */
    Syntax<C> orphanedComments(List<String> comments);

    /**
     * Copies all orphaned comments from the given syntax unit.
     *
     * @param copyFrom
     *     Copy orphaned comments from this unit.
     *
     * @return this, for chaining.
     */
    Syntax<C> orphanedComments(Syntax<?> copyFrom);

    /**
     * Gets all orphaned comments (comments that appear after or at the end of the unit).
     * <p/>
     * A comment is considered <em>orphaned</em> if it does not appear before a logically associated unit. For example, comments
     * at the end of a stylesheet or declaration block.
     *
     * @return The list of comments. Never returns null.
     */
    ImmutableList<Comment> orphanedComments();
}
