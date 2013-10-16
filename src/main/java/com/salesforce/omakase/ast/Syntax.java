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

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SimpleSelector;
import com.salesforce.omakase.broadcast.BroadcastRequirement;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
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
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "parent interface of all subscribable units", broadcasted = BroadcastRequirement.SPECIAL)
public interface Syntax extends Writable, Broadcastable {
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
     * Adds the given comments to this unit.
     * <p/>
     * Note that in the case of {@link Selector}s, it is preferred to add comments to the {@link Selector} object itself instead
     * of the individual {@link SimpleSelector}s inside of it. Likewise, it is preferred to add a comment to the {@link
     * Declaration} itself instead of the property name or value inside of it.
     *
     * @param commentsToAdd
     *     The comments to add.
     *
     * @return this, for chaining.
     */
    Syntax comments(Iterable<String> commentsToAdd);

    /**
     * Adds the given comments to this unit.
     * <p/>
     * Note that in the case of {@link Selector}s, it is preferred to add comments to the {@link Selector} object itself instead
     * of the individual {@link SimpleSelector}s inside of it. Likewise, it is preferred to add a comment to the {@link
     * Declaration} itself instead of the property name or value inside of it.
     *
     * @param commentsToAdd
     *     The comments to add.
     *
     * @return this, for chaining.
     */
    Syntax directComments(Iterable<Comment> commentsToAdd);

    /**
     * Gets all comments <em>associated</em> with this {@link Syntax} unit.
     * <p/>
     * A unit is associated with all comments that directly precede it. In the case of selectors, both the {@link Selector} object
     * and the first {@link SimpleSelector} within the {@link Selector} object will return the same comments.
     *
     * @return The list of comments. Never returns null.
     */
    List<Comment> comments();
}
