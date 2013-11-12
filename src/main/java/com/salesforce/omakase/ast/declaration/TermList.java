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

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.declaration.TermListParser;
import com.salesforce.omakase.util.As;
import com.salesforce.omakase.util.Copy;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * The generic and default {@link Declaration}'s {@link PropertyValue}. This contains a list of {@link Term}s, for example
 * numbers, keywords, functions, hex colors, etc...
 * <p/>
 * You can add new members to this term list via {@link #append(TermListMember)}, or by utilizing the {@link SyntaxCollection}
 * returned by the {@link #members()} method.
 * <p/>
 * In the CSS 2.1 spec this is called "expr", which is obviously shorthand for "expression", however "expression" is name now
 * given to multiple syntax units within different CSS3 modules! So that's why this is not called expression.
 *
 * @author nmcwilliams
 * @see Term
 * @see TermListParser
 * @see TermListMember
 */
@Subscribable
@Description(value = "default, generic property value", broadcasted = REFINED_DECLARATION)
public final class TermList extends AbstractPropertyValue {
    private final SyntaxCollection<TermList, TermListMember> members;
    private boolean important;

    /** Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units). */
    public TermList() {
        this(-1, -1, null);
    }

    /**
     * Constructs a new {@link TermList} instance.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param broadcaster
     *     Used to broadcast new units.
     */
    public TermList(int line, int column, Broadcaster broadcaster) {
        super(line, column);
        members = StandardSyntaxCollection.create(this, broadcaster);
    }

    /**
     * Adds a new {@link TermListMember} to this {@link TermList}.
     *
     * @param member
     *     The member to add.
     *
     * @return this, for chaining.
     */
    public TermList append(TermListMember member) {
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
    public TermList append(OperatorType type) {
        append(new Operator(type));
        return this;
    }

    /**
     * Gets the {@link SyntaxCollection} of {@link TermListMember}s. Use this to append, prepend or otherwise reorganize the terms
     * and operators in this {@link TermList}.
     *
     * @return The {@link SyntaxCollection} instance.
     */
    public SyntaxCollection<TermList, TermListMember> members() {
        return members;
    }

    /**
     * Gets the definitive list of {@link Term}s currently in this {@link TermList} (as opposed to {@link #members()} which
     * returns both terms and operators).
     * <p/>
     * If you are doing validation then most likely this is what you want to use over {@link #members()}. This is the method you
     * must use to get the complete list of the terms contained within this {@link TermList}. Otherwise, there may be terms
     * contained within {@link TermView} instances that are not intuitively accessible from the {@link #members()} method.
     * <p/>
     * This is not to be used for adding or modifying which terms are in this list, however. In that case use {@link #append
     * (TermListMember)} or the {@link #members()} method.
     *
     * @return List of all {@link Term}s.
     */
    public ImmutableList<Term> terms() {
        ImmutableList.Builder<Term> builder = ImmutableList.builder();

        for (TermListMember member : members) {
            if (member instanceof TermView) {
                builder.addAll(((TermView)member).terms());
            } else if (member instanceof Term) {
                builder.add((Term)member);
            }
        }

        return builder.build();
    }

    @Override
    public boolean isImportant() {
        return important;
    }

    @Override
    public PropertyValue important(boolean important) {
        this.important = important;
        return this;
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
        for (TermListMember member : members) {
            writer.writeInner(member, appendable);
        }

        if (important) {
            appendable.spaceIf(writer.isVerbose());
            appendable.append("!important");
        }
    }

    @Override
    public TermList copy() {
        // TESTME
        TermList copy = Copy.comments(this, new TermList());
        copy.important(isImportant());
        for (TermListMember member : members) {
            copy.append(member.copy());
        }
        return copy;
    }

    @Override
    public TermList copyWithPrefix(Prefix prefix, SupportMatrix support) {
        // TESTME
        TermList copy = Copy.comments(this, new TermList());
        copy.important(isImportant());
        for (TermListMember member : members) {
            copy.append(member.copyWithPrefix(prefix, support));
        }
        return copy;
    }

    @Override
    public String toString() {
        return As.string(this)
            .add("abstract", super.toString())
            .add("members", members)
            .addIf(important, "important", important)
            .toString();
    }

    /**
     * Creates a new {@link TermList} with the given {@link Term} as the only member.
     * <p/>
     * Example:
     * <pre>
     * <code>TermList.singleValue(NumericalValue.of(10, "px"));</code>
     * </pre>
     *
     * @param term
     *     The value.
     *
     * @return The new {@link TermList} instance.
     */
    public static TermList singleValue(Term term) {
        return new TermList().append(term);
    }

    /**
     * Creates a new {@link TermList} with multiple values separated by the given {@link OperatorType}.
     * <p/>
     * Example:
     * <pre>
     * <code>NumericalValue px10 = NumericalValue.of(10, "px");
     * NumericalValue em5 = NumericalValue.of(5, "em");
     * PropertyValue value = TermList.ofValues(OperatorType.SPACE, px10, em5);
     * </code>
     * </pre>
     *
     * @param separator
     *     The {@link OperatorType} to place in between each {@link Term}.
     * @param values
     *     List of member {@link Term}s.
     *
     * @return The new {@link TermList} instance.
     */
    public static TermList ofValues(OperatorType separator, Term... values) {
        TermList termList = new TermList();
        for (int i = 0; i < values.length; i++) {
            if (i != 0) termList.append(separator);
            termList.append(values[i]);
        }
        return termList;
    }
}
