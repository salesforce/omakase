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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SimpleSelector;
import com.salesforce.omakase.broadcast.BroadcastRequirement;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
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
@Subscribable
@Description(broadcasted = BroadcastRequirement.SPECIAL, value = "top level interface for all units")
public interface Syntax<C> extends Writable, Broadcastable {
    /**
     * Gets the unique identifier for this unit. This can be used as a key in maps or in any other case where storing a short
     * identifier is preferable.
     *
     * @return The unique identifier.
     */
    int id();

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
     * Adds the given {@link Comment} to this unit.
     * <p/>
     * Note that in the case of {@link Selector}s, it is preferred to add comments to the {@link Selector} object itself instead
     * of the individual {@link SimpleSelector}s inside of it. Likewise, it is preferred to add a comment to the {@link
     * Declaration} itself instead of the property name or value inside of it.
     *
     * @param comment
     *     The comment to add.
     *
     * @return this, for chaining.
     */
    Syntax<C> comment(Comment comment);

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
     * A unit is associated with all comments that directly precede it. However in the case of the <em>first</em> {@link
     * SimpleSelector} within a {@link Selector}, it is the {@link Selector} that will contain the comment instead.
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

    /**
     * Checks if this unit has a CSS comment annotation with the given name.
     * <p/>
     * CSS comment annotations are CSS comments that contain an annotation in the format of "@annotationName [optionalArgs]*", for
     * example "@noparse", "@browser ie7", etc...
     * <p/>
     * CSS comment annotations cannot be mixed with textual comments and there can be at most one annotation per comment block.
     * CSS comment annotations can have optional arguments, separated by spaces, with a maximum of five arguments allowed.
     * <p/>
     * Any comments that precede this unit in the source code will be checked for the annotation. However in the case of the
     * <em>first</em> {@link SimpleSelector} within a {@link Selector}, it is the {@link Selector} that will contain the
     * annotation instead. For more information see the main readme file.
     *
     * @param name
     *     Check for an annotation with this name.
     *
     * @return True if a {@link CssAnnotation} was found with the given name in {@link Comment}s associated with this unit.
     */
    boolean hasAnnotation(String name);

    /**
     * Checks if this unit has a CSS comment with a {@link CssAnnotation} that equals the given one.
     * <p/>
     * This is most useful in tangent with the {@link #annotate(CssAnnotation)} method. You can annotate many syntax units using
     * that method and then subsequently check for the annotation using this method, reusing the same instance in all cases for
     * efficiency.
     * <p/>
     * The annotation must match according to the rules defined in {@link CssAnnotation#equals(Object)}.
     * <p/>
     * In additional to dynamically added annotations, this will also match against regular comments from the source file. Any
     * comments that precede this unit in the source code will be checked for the annotation. However in the case of the
     * <em>first</em> {@link SimpleSelector} within a {@link Selector}, it is the {@link Selector} that will contain the
     * annotation instead. For more information see the main readme file.
     *
     * @param annotation
     *     Check for a {@link CssAnnotation} that equals this one.
     *
     * @return True if a {@link CssAnnotation} was found that equals the given one.
     */
    boolean hasAnnotation(CssAnnotation annotation);

    /**
     * Gets the {@link CssAnnotation} with the given name from the comments associated with this unit, if there is one.
     * <p/>
     * CSS comment annotations are CSS comments that contain an annotation in the format of "@annotationName [optionalArgs]*", for
     * example "@noparse", "@browser ie7", etc...
     * <p/>
     * CSS comment annotations cannot be mixed with textual comments and there can be at most one annotation per comment block.
     * CSS comment annotations can have optional arguments, separated by spaces, with a maximum of five arguments allowed.
     * <p/>
     * Any comments that precede this unit in the source code will be checked for the annotation. However in the case of the
     * <em>first</em> {@link SimpleSelector} within a {@link Selector}, it is the {@link Selector} that will contain the
     * annotation instead. For more information see the main readme file.
     *
     * @param name
     *     Get the annotation with this name.
     *
     * @return The {@link CssAnnotation}, or {@link Optional#absent()} if not found.
     */
    Optional<CssAnnotation> annotation(String name);

    /**
     * Gets all {@link CssAnnotation}s from the comments associated with this unit.
     * <p/>
     * CSS comment annotations are CSS comments that contain an annotation in the format of "@annotationName [optionalArgs]*", for
     * example "@noparse", "@browser ie7", etc...
     * <p/>
     * CSS comment annotations cannot be mixed with textual comments and there can be at most one annotation per comment block.
     * CSS comment annotations can have optional arguments, separated by spaces, with a maximum of five arguments allowed.
     * <p/>
     * Any comments that precede this unit in the source code will be checked for the annotation.However in the case of the
     * <em>first</em> {@link SimpleSelector} within a {@link Selector}, it is the {@link Selector} that will contain the
     * annotation instead. For more information see the main readme file.
     *
     * @return All found {@link CssAnnotation}s.
     */
    List<CssAnnotation> annotations();

    /**
     * Appends the given {@link CssAnnotation} to this unit.
     * <p/>
     * A {@link Comment} will be created and appended to this unit using the normal CSS comment annotation syntax. This means if
     * CSS comments are written out then they will include this annotation. The comment will also be returned by normal comment
     * retrieval methods such as {@link #comments()}.
     * <p/>
     * You can subsequently check for this annotation again using the {@link #hasAnnotation(CssAnnotation)} method. This might be
     * useful in plugins that dynamically annotate syntax units and then subsequently check for the annotation later on, as using
     * both of these methods can efficiently reuse the same {@link CssAnnotation} instance.
     *
     * @param annotation
     *     Append this annotation.
     */
    void annotate(CssAnnotation annotation);

    /**
     * Specifies whether this object will handle writing its own comments, instead of the automatic behavior of the {@link
     * StyleWriter}.
     * <p/>
     * If returning true, be sure to check {@link StyleWriter#shouldWriteComments()} to determine if comments should actually be written
     * out or not. The {@link StyleWriter#appendComments(Iterable, StyleWriter, StyleAppendable)} utility method contains this
     * logic and is the preferable way to handle it.
     *
     * @return True if this object writes its own comments.
     */
    boolean writesOwnComments();

    /**
     * Specifies whether this object will handle writing its own orphaned comments, instead of the automatic behavior of the
     * {@link StyleWriter}.
     * <p/>
     * If returning true, be sure to check {@link StyleWriter#shouldWriteComments()} to determine if comments should actually be written
     * out or not. The {@link StyleWriter#appendComments(Iterable, StyleWriter, StyleAppendable)} utility method contains this
     * logic and is the preferable way to handle it.
     *
     * @return True if this object writes its own comments.
     */
    boolean writesOwnOrphanedComments();
}
