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

package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
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
     * <p>
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
    public Combinator copy() {
        return new Combinator(type).copiedFrom(this);
    }

    /**
     * Creates a new descendant {@link Combinator} (" "). Usually used when dynamically creating {@link Selector}s.
     * <p>
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
     * Creates a new child {@link Combinator} ("&gt;"). Usually used when dynamically creating {@link Selector}s.
     * <p>
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
     * <p>
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
     * <p>
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
