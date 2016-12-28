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

package com.salesforce.omakase.ast.declaration;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.declaration.PropertyValueParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * The value of a property in a {@link Declaration}.
 * <p>
 * This contains a list of {@link Term}s, for example numbers, keywords, functions, hex colors, etc...
 * <p>
 * You can add new members to this via {@link #append(PropertyValueMember)}, or by utilizing the {@link SyntaxCollection} returned
 * by the {@link #members()} method.
 * <p>
 * In the CSS 2.1 spec this is called "expr", which is obviously shorthand for "expression", however "expression" is name now
 * given to multiple syntax units within different CSS3 modules! So that's why this is not called expression.
 *
 * @author nmcwilliams
 * @see Term
 * @see PropertyValueParser
 * @see PropertyValueMember
 */
@Subscribable
@Description(value = "interface for all property values", broadcasted = REFINED_DECLARATION)
public final class PropertyValue extends AbstractSyntax {
    private final SyntaxCollection<PropertyValue, PropertyValueMember> members;
    private transient Declaration declaration;
    private boolean important;

    /** Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units). */
    public PropertyValue() {
        this(-1, -1);
    }

    /**
     * Constructs a new {@link PropertyValue} instance.
     *  @param line
     *     The line number.
     * @param column
     *     The column number.
     */
    public PropertyValue(int line, int column) {
        super(line, column);
        members = new LinkedSyntaxCollection<>(this);
    }

    /**
     * Adds a new {@link PropertyValueMember} to this {@link PropertyValue}.
     *
     * @param member
     *     The member to add.
     *
     * @return this, for chaining.
     */
    public PropertyValue append(PropertyValueMember member) {
        members.append(member);
        return this;
    }

    /**
     * Adds a new {@link Operator} with the given {@link OperatorType}.
     *
     * @param type
     *     The {@link OperatorType}.
     *
     * @return this, for chaining.
     */
    public PropertyValue append(OperatorType type) {
        append(new Operator(type));
        return this;
    }

    /**
     * Gets the {@link SyntaxCollection} of {@link PropertyValueMember}s. Use this to append, prepend or otherwise reorganize the
     * terms and operators in this {@link PropertyValue}.
     *
     * @return The {@link SyntaxCollection} instance.
     */
    public SyntaxCollection<PropertyValue, PropertyValueMember> members() {
        return members;
    }

    /**
     * Gets the list of {@link Term}s currently in this {@link PropertyValue} (as opposed to {@link #members()} which returns both
     * terms and operators).
     *
     * @return List of all {@link Term}s.
     */
    public ImmutableList<Term> terms() {
        ImmutableList.Builder<Term> builder = ImmutableList.builder();

        for (PropertyValueMember member : members) {
            if (member instanceof Term) builder.add((Term)member);
        }

        return builder.build();
    }

    /**
     * Does a quick count of the number of {@link Term}s (does not include operators) within this {@link PropertyValue}.
     *
     * @return The number of {@link Term}s.
     */
    public long countTerms() {
        return members.stream().filter(PropertyValueMember::isTerm).count();
    }

    /**
     * Gets the <em>textual</em> content of the only {@link Term} within this {@link PropertyValue}. The returned {@link Optional}
     * will only be present if this {@link PropertyValue} contains exactly one {@link Term}!
     * <p>
     * This method may be useful as a generic way of getting the value of unknown or potentially varying term types.
     * <p>
     * <b>Important:</b> this is not a substitute or a replica of how the term or this property value will actually be written to
     * a stylesheet. The textual content returned may not include certain tokens and outer symbols such as hashes, quotes,
     * parenthesis, etc... . To get the textual content as it would be written to a stylesheet see {@link StyleWriter#writeSingle
     * (Writable)} instead. However note that you should rarely have need for doing that outside of actually creating stylesheet
     * output.
     * <p>
     * {@link KeywordValue}s will simply return the keyword, {@link StringValue}s will return the contents of the string <b>not
     * including quotes</b>, functions will return the content of the function not including the parenthesis, {@link
     * HexColorValue} will return the hex value without the leading '#' , and so on... See each specific {@link Term}
     * implementation for more details.
     * <p>
     * <b>Important:</b> if this property value has more than one term then this method will return an empty {@link Optional}. It
     * will not concatenate term values.
     *
     * @return The textual content, or an empty {@link Optional} if there is more than one or no terms present.
     *
     * @see Term#textualValue()
     */
    public Optional<String> singleTextualValue() {
        ImmutableList<Term> terms = terms();
        return terms.size() == 1 ? Optional.of(terms.get(0).textualValue()) : Optional.empty();
    }

    /**
     * Gets whether this {@link PropertyValue} is marked as "!important".
     *
     * @return True if this property value is marked as important.
     */
    public boolean isImportant() {
        return important;
    }

    /**
     * Sets whether this {@link PropertyValue} is marked as "!important".
     *
     * @param important
     *     Whether the value is "!important".
     *
     * @return this, for chaining.
     */
    public PropertyValue important(boolean important) {
        this.important = important;
        return this;
    }

    /**
     * Sets the parent {@link Declaration}. Generally this is handled automatically when this property value is set on the {@link
     * Declaration}, so it is not recommended to call this method manually. If you do, results may be unexpected.
     *
     * @param parent
     *     The {@link Declaration} that contains this property.
     */
    public void declaration(Declaration parent) {
        this.declaration = parent;
    }

    /**
     * Gets the parent {@link Declaration} that owns this property. This will not be set for dynamically created property values
     * not yet added to a {@link Declaration} instance.
     *
     * @return The parent {@link Declaration}. If working with this term before it has been properly linked then this may return
     * null. This is not the case for normal subscription methods.
     */
    public Declaration declaration() {
        return declaration;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster, Status status) {
        if (status() == status) {
            members.propagateBroadcast(broadcaster, status);
            if (!members.isEmpty()) {
                super.propagateBroadcast(broadcaster, status);
            }
        }
    }

    @Override
    public boolean isWritable() {
        return super.isWritable() && !members.isEmptyOrNoneWritable();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        PropertyValueMember previous = null;
        for (PropertyValueMember member : members) {
            appendable.spaceIf(previous instanceof Term && member instanceof Term);
            writer.writeInner(member, appendable);
            previous = member;
        }

        if (important) {
            appendable.spaceIf(writer.isVerbose()).append("!important");
        }
    }

    @Override
    public PropertyValue copy() {
        PropertyValue copy = new PropertyValue().important(important).copiedFrom(this);
        for (PropertyValueMember member : members) {
            copy.append(member.copy());
        }
        return copy;
    }

    /**
     * Creates a new {@link PropertyValue} with the given {@link Term} as the only member.
     * <p>
     * Example:
     * <pre>
     * <code>PropertyValue.of(NumericalValue.of(10, "px"));</code>
     * </pre>
     *
     * @param term
     *     The term.
     *
     * @return The new {@link PropertyValue} instance.
     */
    public static PropertyValue of(Term term) {
        return new PropertyValue().append(checkNotNull(term, "term cannot be null"));
    }

    /**
     * Creates a new {@link PropertyValue} with multiple terms. No operators will be placed between the terms.
     * <p>
     * Example:
     * <pre>
     * <code>NumericalValue px10 = NumericalValue.of(10, "px");
     * NumericalValue em5 = NumericalValue.of(5, "em");
     * PropertyValue value = PropertyValue.of(px10, em5);
     * </code>
     * </pre>
     *
     * @param term
     *     The first term.
     * @param terms
     *     Additional terms.
     *
     * @return The new {@link PropertyValue} instance.
     */
    public static PropertyValue of(Term term, Term... terms) {
        PropertyValue value = new PropertyValue();
        value.append(checkNotNull(term, "the first term cannot be null"));

        for (Term t : terms) {
            value.append(checkNotNull(t, "terms cannot be null"));
        }

        return value;
    }

    /**
     * Creates a new {@link PropertyValue} with multiple terms separated by the given {@link OperatorType}.
     * <p>
     * Example:
     * <pre>
     * <code>NumericalValue px10 = NumericalValue.of(10, "px");
     * NumericalValue em5 = NumericalValue.of(5, "em");
     * PropertyValue value = PropertyValue.of(OperatorType.SLASH, px10, em5);
     * </code>
     * </pre>
     *
     * @param separator
     *     The {@link OperatorType} to place in between each {@link Term}.
     * @param term
     *     The first term.
     * @param terms
     *     Additional terms.
     *
     * @return The new {@link PropertyValue} instance.
     */
    public static PropertyValue of(OperatorType separator, Term term, Term... terms) {
        PropertyValue value = new PropertyValue();
        value.append(checkNotNull(term, "the first term cannot be null"));

        for (Term t : terms) {
            value.append(separator);
            value.append(checkNotNull(t, "terms cannot be null"));
        }

        return value;
    }

    /**
     * <p>
     * Creates a new {@link PropertyValue} with one or more terms and operators.
     * Example:
     * <pre>
     * <code>NumericalValue px10 = NumericalValue.of(10, "px");
     * NumericalValue em5 = NumericalValue.of(5, "em");
     * PropertyValue value = PropertyValue.of(px10, OperatorType.SLASH, em5);
     * </code>
     * </pre>
     *
     * @param term
     *     The first term.
     * @param members
     *     The additional terms and operators.
     *
     * @return The new {@link PropertyValue} instance.
     */
    public static PropertyValue of(Term term, PropertyValueMember... members) {
        PropertyValue value = new PropertyValue();
        value.append(checkNotNull(term, "the first term cannot be null"));

        for (PropertyValueMember member : members) {
            value.append(checkNotNull(member, "members cannot be null"));
        }

        return value;
    }
}
