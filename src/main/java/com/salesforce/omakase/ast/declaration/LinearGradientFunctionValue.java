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
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * Represents a linear-gradient (or repeating-linear-gradient) function.
 * <p/>
 * Use {@link #repeating(boolean)} if the gradient is a repeating-linear-gradient.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "linear gradient function", broadcasted = REFINED_DECLARATION)
public final class LinearGradientFunctionValue extends AbstractTerm implements FunctionValue {
    private String args;
    private boolean repeating;
    private Optional<Prefix> prefix = Optional.absent();

    /**
     * Constructs a new instance of a {@link LinearGradientFunctionValue}.
     * <p/>
     * If dynamically creating a new instance then use {@link #LinearGradientFunctionValue(String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param args
     *     The raw function args.
     */
    public LinearGradientFunctionValue(int line, int column, String args) {
        super(line, column);
        this.args = args;
    }

    /**
     * Constructs a new instance of a {@link LinearGradientFunctionValue} (used for dynamically created {@link Syntax} units).
     *
     * @param args
     *     The raw function args.
     */
    public LinearGradientFunctionValue(String args) {
        args(args);
    }

    /**
     * Sets the function arguments.
     *
     * @param args
     *     The raw function args.
     *
     * @return this, for chaining.
     */
    public LinearGradientFunctionValue args(String args) {
        this.args = checkNotNull(args, "args cannot be null");
        return this;
    }

    /**
     * Gets the raw function arguments.
     *
     * @return The function arguments.
     */
    public String args() {
        return args;
    }

    /**
     * Sets whether this linear-gradient is a repeating-linear-gradient.
     *
     * @param repeating
     *     Specify true to indicate this is a repeating-linear-gradient.
     *
     * @return this, for chaining.
     */
    public LinearGradientFunctionValue repeating(boolean repeating) {
        this.repeating = repeating;
        return this;
    }

    /**
     * Gets whether this is a repeating-linear-gradient.
     *
     * @return True if this is a repeating-linear-gradient.
     */
    public boolean repeating() {
        return repeating;
    }

    /**
     * Sets the vendor prefix.
     *
     * @param prefix
     *     The prefix, or null to remove.
     *
     * @return this, for chaining.
     */
    public LinearGradientFunctionValue prefix(Prefix prefix) {
        this.prefix = Optional.fromNullable(prefix);
        return this;
    }

    /**
     * Gets the prefix, if present.
     *
     * @return The prefix, or {@link Optional#absent()} if no prefix exists.
     */
    public Optional<Prefix> prefix() {
        return prefix;
    }

    /**
     * Gets the raw function arguments. Prefer to use {@link #args()}, which is identical to this method.
     *
     * @return The function arguments.
     */
    @Override
    public String textualValue() {
        return args;
    }

    @Override
    public String name() {
        StringBuilder builder = new StringBuilder(32);
        if (prefix.isPresent()) {
            builder.append(prefix.get());
        }
        return builder.append(unprefixedName()).toString();
    }

    /**
     * Gets the name of the function without the prefix, is present.
     *
     * @return The name of the function.
     */
    public String unprefixedName() {
        return repeating ? "repeating-linear-gradient" : "linear-gradient";
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(name()).append('(').append(args).append(')');
    }

    @Override
    public LinearGradientFunctionValue copy() {
        return new LinearGradientFunctionValue(args).repeating(repeating).copiedFrom(this);
    }
}
