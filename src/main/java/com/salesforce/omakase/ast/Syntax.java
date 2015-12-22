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

package com.salesforce.omakase.ast;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SimpleSelector;
import com.salesforce.omakase.broadcast.BroadcastRequirement;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
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
 * @author nmcwilliams
 */
@Subscribable
@Description(broadcasted = BroadcastRequirement.SPECIAL, value = "top level interface for all units")
public interface Syntax extends Writable, Broadcastable {
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
    Syntax copy();

    /**
     * Returns a string representation of this object.
     *
     * @param includeUnitType
     *     Specify true to append in parenthesis the syntax type (e.g., 'pseudo-element-selector').
     *
     * @return The string.
     */
    String toString(boolean includeUnitType);

    /**
     * Adds the given comment to this unit.
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
    Syntax comment(String comment);

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
    Syntax comment(Comment comment);

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
    Syntax comments(List<String> comments);

    /**
     * Copies all comments from the given syntax unit.
     *
     * @param copyFrom
     *     Copy comments from this unit.
     *
     * @return this, for chaining.
     */
    Syntax comments(Syntax copyFrom);

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
    Syntax orphanedComments(List<String> comments);

    /**
     * Copies all orphaned comments from the given syntax unit.
     *
     * @param copyFrom
     *     Copy orphaned comments from this unit.
     *
     * @return this, for chaining.
     */
    Syntax orphanedComments(Syntax copyFrom);

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
     * Same as {@link #annotate(CssAnnotation)}, except only if {@link #hasAnnotation(CssAnnotation)} is false for the given
     * annotation.
     *
     * @param annotation
     *     Add this annotation.
     */
    void annotateUnlessPresent(CssAnnotation annotation);

    /**
     * Specifies whether this object will handle writing its own comments, instead of the automatic behavior of the {@link
     * StyleWriter}.
     * <p/>
     * If returning true, be sure to check {@link StyleWriter#shouldWriteAllComments()} to determine if comments should actually
     * be written out or not. The {@link StyleWriter#appendComments(Iterable, StyleAppendable)} utility method contains this logic
     * and is the preferable way to handle it.
     *
     * @return True if this object writes its own comments.
     */
    boolean writesOwnComments();

    /**
     * Specifies whether this object will handle writing its own orphaned comments, instead of the automatic behavior of the
     * {@link StyleWriter}.
     * <p/>
     * If returning true, be sure to check {@link StyleWriter#shouldWriteAllComments()} to determine if comments should actually
     * be written out or not. The {@link StyleWriter#appendComments(Iterable, StyleAppendable)} utility method contains this logic
     * and is the preferable way to handle it.
     *
     * @return True if this object writes its own comments.
     */
    boolean writesOwnOrphanedComments();
}
