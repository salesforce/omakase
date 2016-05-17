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

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.util.Args;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * Represents a linear-gradient (or repeating-linear-gradient) function.
 * <p>
 * Use {@link #repeating(boolean)} if the gradient is a repeating-linear-gradient.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "linear gradient function", broadcasted = REFINED_DECLARATION)
public final class LinearGradientFunctionValue extends AbstractTerm implements FunctionValue {
    private String args;
    private boolean repeating;
    private Prefix prefix;

    /**
     * Constructs a new instance of a {@link LinearGradientFunctionValue}.
     * <p>
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
        this.prefix = prefix;
        return this;
    }

    /**
     * Gets the prefix, if present.
     *
     * @return The prefix, or {@link Optional#absent()} if no prefix exists.
     */
    public Optional<Prefix> prefix() {
        return Optional.fromNullable(prefix);
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
        if (prefix != null) {
            builder.append(prefix);
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
        appendable.append(name()).append('(');
        appendable.append(writer.isVerbose() ? args : Args.clean(args));
        appendable.append(')');
    }

    @Override
    public LinearGradientFunctionValue copy() {
        return new LinearGradientFunctionValue(args).repeating(repeating).copiedFrom(this);
    }
}
