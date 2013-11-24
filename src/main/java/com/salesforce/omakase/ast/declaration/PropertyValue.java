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

package com.salesforce.omakase.ast.declaration;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.declaration.PropertyValueParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * The value of a property in a {@link Declaration}.
 * <p/>
 * This contains a list of {@link Term}s, for example numbers, keywords, functions, hex colors, etc...
 * <p/>
 * You can add new members to this term list via {@link #append(PropertyValueMember)}, or by utilizing the {@link
 * SyntaxCollection} returned by the {@link #members()} method.
 * <p/>
 * In the CSS 2.1 spec this is called "expr", which is obviously shorthand for "expression", however "expression" is name now
 * given to multiple syntax units within different CSS3 modules! So that's why this is not called expression.
 *
 * @author nmcwilliams
 * @author nmcwilliams
 * @see Term
 * @see PropertyValueParser
 * @see PropertyValueMember
 */
@Subscribable
@Description(value = "interface for all property values", broadcasted = REFINED_DECLARATION)
public final class PropertyValue extends AbstractSyntax<PropertyValue> {
    private final SyntaxCollection<PropertyValue, PropertyValueMember> members;
    private transient Optional<Declaration> declaration = Optional.absent();
    private boolean important;

    /** Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units). */
    public PropertyValue() {
        this(-1, -1, null);
    }

    /**
     * Constructs a new {@link PropertyValue} instance.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param broadcaster
     *     Used to broadcast new units.
     */
    public PropertyValue(int line, int column, Broadcaster broadcaster) {
        super(line, column);
        members = new StandardSyntaxCollection<PropertyValue, PropertyValueMember>(this, broadcaster);
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
     * Gets the definitive list of {@link Term}s currently in this {@link PropertyValue} (as opposed to {@link #members()} which
     * returns both terms and operators).
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
        this.declaration = Optional.fromNullable(parent);
    }

    /**
     * Gets the parent {@link Declaration} that owns this property, or absent if not set. This will not be set for dynamically
     * created property values not yet added to a {@link Declaration} instance.
     *
     * @return The parent {@link Declaration}, or {@link Optional#absent()} if not set.
     */
    public Optional<Declaration> declaration() {
        return declaration;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        super.propagateBroadcast(broadcaster);
        members.propagateBroadcast(broadcaster);
    }

    @Override
    public boolean isWritable() {
        return !members.isEmptyOrNoneWritable();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        for (PropertyValueMember member : members) {
            writer.writeInner(member, appendable);
        }

        if (important) {
            appendable.spaceIf(writer.isVerbose()).append("!important");
        }
    }

    @Override
    protected PropertyValue makeCopy(Prefix prefix, SupportMatrix support) {
        PropertyValue copy = new PropertyValue();
        copy.important(important);
        for (PropertyValueMember member : members) {
            copy.append(member.copy(prefix, support));
        }
        return copy;
    }

    /**
     * Creates a new {@link PropertyValue} with the given {@link Term} as the only member.
     * <p/>
     * Example:
     * <pre>
     * <code>PropertyValue.of(NumericalValue.of(10, "px"));</code>
     * </pre>
     *
     * @param term
     *     The value.
     *
     * @return The new {@link PropertyValue} instance.
     */
    public static PropertyValue of(Term term) {
        return new PropertyValue().append(term);
    }

    /**
     * Creates a new {@link PropertyValue} with multiple values separated by the given {@link OperatorType}.
     * <p/>
     * Example:
     * <pre>
     * <code>NumericalValue px10 = NumericalValue.of(10, "px");
     * NumericalValue em5 = NumericalValue.of(5, "em");
     * PropertyValue value = PropertyValue.ofTerms(OperatorType.SPACE, px10, em5);
     * </code>
     * </pre>
     *
     * @param separator
     *     The {@link OperatorType} to place in between each {@link Term}.
     * @param values
     *     List of member {@link Term}s.
     *
     * @return The new {@link PropertyValue} instance.
     */
    public static PropertyValue ofTerms(OperatorType separator, Term... values) {
        PropertyValue value = new PropertyValue();
        for (int i = 0; i < values.length; i++) {
            if (i != 0) value.append(separator);
            value.append(values[i]);
        }
        return value;
    }
}
