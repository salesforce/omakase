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

package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.selector.CombinatorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * Represents a CSS combinator.
 *
 * @author nmcwilliams
 * @see CombinatorParser
 */
public final class Combinator extends AbstractSelectorPart implements SimpleSelector {
    private final CombinatorType type;

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param type
     *     The {@link CombinatorType}.
     */
    public Combinator(CombinatorType type) {
        this(-1, -1, type);
    }

    /**
     * Creates a new instance with the given line and column numbers, and the {@link CombinatorType}.
     * <p/>
     * If dynamically creating a new instance then use {@link #Combinator(CombinatorType)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param type
     *     The {@link CombinatorType}.
     */
    public Combinator(int line, int column, CombinatorType type) {
        super(line, column);
        this.type = type;
        status(Status.NEVER_EMIT);
    }

    @Override
    public SelectorPartType type() {
        switch (type) {
        case DESCENDANT:
            return SelectorPartType.DESCENDANT_COMBINATOR;
        case CHILD:
            return SelectorPartType.CHILD_COMBINATOR;
        case ADJACENT_SIBLING:
            return SelectorPartType.ADJACENT_SIBLING_COMBINATOR;
        case GENERAL_SIBLING:
            return SelectorPartType.GENERAL_SIBLING_COMBINATOR;
        }
        throw new AssertionError("unknown combinator type");
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        switch (type) {
        case DESCENDANT:
            appendable.append(' ');
            break;
        case CHILD:
            appendable.spaceIf(writer.isVerbose());
            appendable.append('>');
            appendable.spaceIf(writer.isVerbose());
            break;
        case ADJACENT_SIBLING:
            appendable.spaceIf(writer.isVerbose());
            appendable.append('+');
            appendable.spaceIf(writer.isVerbose());
            break;
        case GENERAL_SIBLING:
            appendable.spaceIf(writer.isVerbose());
            appendable.append('~');
            appendable.spaceIf(writer.isVerbose());
            break;
        }
    }

    @Override
    protected Combinator makeCopy(Prefix prefix, SupportMatrix support) {
        return new Combinator(type);
    }

    /**
     * Creates a new descendant {@link Combinator} (" "). Usually used when dynamically creating {@link Selector}s.
     * <p/>
     * Example:
     * <pre>
     * {@code new Selector(new IdSelector("name"), Combinator.descendant(), new ClassSelector("myClass"));}
     * </pre>
     *
     * @return The new {@link Combinator} instance.
     */
    public static Combinator descendant() {
        return new Combinator(CombinatorType.DESCENDANT);
    }

    /**
     * Creates a new child {@link Combinator} (">"). Usually used when dynamically creating {@link Selector}s.
     * <p/>
     * Example:
     * <pre>
     * {@code new Selector(new IdSelector("name"), Combinator.child(), new ClassSelector("myClass"));}
     * </pre>
     *
     * @return The new {@link Combinator} instance.
     */
    public static Combinator child() {
        return new Combinator(CombinatorType.CHILD);
    }

    /**
     * Creates a new adjacent sibling {@link Combinator} ("+"). Usually used when dynamically creating {@link Selector}s.
     * <p/>
     * Example:
     * <pre>
     * {@code new Selector(new IdSelector("name"), Combinator.adjacent(), new ClassSelector("myClass"));}
     * </pre>
     *
     * @return The new {@link Combinator} instance.
     */
    public static Combinator adjacent() {
        return new Combinator(CombinatorType.ADJACENT_SIBLING);
    }

    /**
     * Creates a new general sibling {@link Combinator} ("~"). Usually used when dynamically creating {@link Selector}s.
     * <p/>
     * Example:
     * <pre>
     * {@code new Selector(new IdSelector("name"), Combinator.general(), new ClassSelector("myClass"));}
     * </pre>
     *
     * @return The new {@link Combinator} instance.
     */
    public static Combinator general() {
        return new Combinator(CombinatorType.GENERAL_SIBLING);
    }
}
