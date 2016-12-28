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

import com.salesforce.omakase.ast.Named;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.selector.PseudoSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.Optional;

import static com.google.common.base.Preconditions.*;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_SELECTOR;

/**
 * Represents a CSS pseudo class selector.
 * <p>
 * Note that even though some pseudo elements can be written using the pseudo class format, they are <b>not</b> considered pseudo
 * classes in this parser, but as {@link PseudoElementSelector}s.
 *
 * @author nmcwilliams
 * @see PseudoSelectorParser
 */
@Subscribable
@Description(value = "pseudo class selector segment", broadcasted = REFINED_SELECTOR)
public final class PseudoClassSelector extends AbstractSelectorPart implements SimpleSelector, Named {
    private String name;
    private String args;

    /**
     * Constructs a new {@link PseudoClassSelector} instance with the given name and optional args.
     * <p>
     * If dynamically creating a new instance then use {@link #PseudoClassSelector(String)} or {@link #PseudoClassSelector(String,
     * String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param name
     *     Name of the pseudo class.
     * @param args
     *     Optional arguments for the pseudo class selector (null is ok for no args).
     */
    public PseudoClassSelector(int line, int column, String name, String args) {
        super(line, column);
        this.name = name;
        this.args = args;
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param name
     *     Name of the pseudo class.
     */
    public PseudoClassSelector(String name) {
        name(name);
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param name
     *     Name of the pseudo class.
     * @param args
     *     The arguments (not including the parenthesis).
     */
    public PseudoClassSelector(String name, String args) {
        name(name).args(args);
    }

    /**
     * Sets the name of the selector (e.g., "hover").
     *
     * @param name
     *     The new name.
     *
     * @return this, for chaining.
     */
    public PseudoClassSelector name(String name) {
        checkArgument(!PseudoElementSelector.POSERS.contains(name),
            String.format("%s must be created as a PseudoElementSelector", name));

        this.name = checkNotNull(name, "name cannot be null");
        return this;
    }

    @Override
    public String name() {
        return name;
    }

    /**
     * Specifies the arguments of the pseudo selector (e.g., "2n+1").
     * <p>
     * If null is specified then the arguments will be removed.
     *
     * @param args
     *     The arguments (not including the parenthesis).
     *
     * @return this, for chaining.
     */
    public PseudoClassSelector args(String args) {
        this.args = args;
        return this;
    }

    /**
     * Gets the arguments of the pseudo selector (e.g., "2n+1").
     *
     * @return The arguments, or an empty {@link Optional} if not present.
     */
    public Optional<String> args() {
        return Optional.ofNullable(args);
    }

    @Override
    public SelectorPartType type() {
        return SelectorPartType.PSEUDO_CLASS_SELECTOR;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(':').append(name);

        if (args != null) {
            appendable.append('(').append(args).append(')');
        }
    }

    @Override
    public PseudoClassSelector copy() {
        return new PseudoClassSelector(name, args).copiedFrom(this);
    }
}
