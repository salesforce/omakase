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

import com.google.common.base.Optional;
import com.salesforce.omakase.util.As;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.selector.PseudoSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.*;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_SELECTOR;

/**
 * Represents a CSS pseudo class selector.
 * <p/>
 * Note that even though some pseudo elements can be written using the pseudo class format, they are <b>not</b> considered pseudo
 * classes in this parser, but as {@link PseudoElementSelector}s.
 *
 * @author nmcwilliams
 * @see PseudoSelectorParser
 */
@Subscribable
@Description(value = "pseudo class selector segment", broadcasted = REFINED_SELECTOR)
public final class PseudoClassSelector extends AbstractSelectorPart implements SimpleSelector {
    private String name;
    private Optional<String> args = Optional.absent();

    /**
     * Constructs a new {@link PseudoClassSelector} instance with the given name and optional args.
     * <p/>
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
        this.args = Optional.fromNullable(args);
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
        name(name);
        args(args);
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

    /**
     * Gets the selector name (e.g., "hover").
     *
     * @return The selector name.
     */
    public String name() {
        return name;
    }

    /**
     * Specifies the arguments of the pseudo selector (e.g., "2n+1").
     * <p/>
     * If null is specified then the arguments will be removed.
     *
     * @param args
     *     The arguments (not including the parenthesis).
     *
     * @return this, for chaining.
     */
    public PseudoClassSelector args(String args) {
        this.args = Optional.fromNullable(args);
        return this;
    }

    /**
     * Gets the arguments of the pseudo selector (e.g., "2n+1").
     *
     * @return The arguments, or {@link Optional#absent()} if not specified.
     */
    public Optional<String> args() {
        return args;
    }

    @Override
    public SelectorPartType type() {
        return SelectorPartType.PSEUDO_CLASS_SELECTOR;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(':').append(name);

        if (args.isPresent()) {
            appendable.append('(').append(args.get()).append(')');
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("abstract", super.toString())
            .add("name", name)
            .addIf(args.isPresent(), "args", args)
            .toString();
    }
}
