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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

import java.io.IOException;

import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.util.Args;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * A generic function value with non-validated arguments.
 *
 * @author nmcwilliams
 * @see FunctionValueParser
 */
@Subscribable
@Description(value = "unknown function value", broadcasted = REFINED_DECLARATION)
public class GenericFunctionValue extends AbstractTerm implements FunctionValue {
    private String name;
    private String args;

    /**
     * Constructs a new {@link GenericFunctionValue} instance with the given function name and arguments.
     * <p>
     * If dynamically creating a new instance then use {@link #GenericFunctionValue(String, String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param name
     *     The name of the function.
     * @param args
     *     The raw, non-validated function arguments.
     */
    public GenericFunctionValue(int line, int column, String name, String args) {
        super(line, column);
        this.name = name;
        this.args = args;
    }

    /**
     * Constructs a new {@link GenericFunctionValue} instance with the given function name and arguments (used for dynamically
     * created {@link Syntax} units).
     *
     * @param name
     *     The name of the function.
     * @param args
     *     The function arguments.
     */
    public GenericFunctionValue(String name, String args) {
        name(name).args(args);
    }

    /**
     * Sets the function name. Note that changing this value conceptually invalidates any previous refinement.
     *
     * @param name
     *     The function name.
     *
     * @return this, for chaining.
     */
    public GenericFunctionValue name(String name) {
        this.name = checkNotNull(name, "name cannot be null");
        return this;
    }

    /**
     * Gets the function name.
     *
     * @return The function name.
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * Sets the raw arguments. Note that changing this value will invalidate any previous refinement.
     *
     * @param args
     *     The arguments.
     *
     * @return this, for chaining.
     */
    public GenericFunctionValue args(String args) {
        this.args = checkNotNull(args, "args cannot be null");
        return this;
    }

    /**
     * Gets the raw arguments.
     *
     * @return The raw arguments.
     */
    public String args() {
        return args;
    }

    /**
     * Gets the raw arguments. Prefer to use {@link #args()}, which is identical to this method.
     *
     * @return The inner arguments.
     */
    @Override
    public String textualValue() {
        return args();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(name).append('(');
        appendable.append(writer.isVerbose() ? args : Args.clean(args));
        appendable.append(')');
    }

    @Override
    public GenericFunctionValue copy() {
        return new GenericFunctionValue(name, args).copiedFrom(this);
    }

    /**
     * Creates a new {@link GenericFunctionValue} instance with the given function name and args.
     *
     * @param name
     *     The name of the function.
     * @param args
     *     The function arguments.
     *
     * @return The new {@link GenericFunctionValue} instance.
     */
    public static GenericFunctionValue of(String name, String args) {
        return new GenericFunctionValue(name, args);
    }
}
