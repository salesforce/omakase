/*
 * Copyright (c) 2017, salesforce.com, inc.
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

package com.salesforce.omakase.ast;

import com.salesforce.omakase.ast.declaration.GenericFunctionValue;
import com.salesforce.omakase.broadcast.BroadcastRequirement;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a function value before refinement.
 * <p>
 * Note that this unit is only broadcasted during the {@link Refine} phase. It is never added to the AST. Subscribe to this type
 * of unit in order to provide custom functions. If a {@link RawFunction} is not handled by a refiner then a {@link
 * GenericFunctionValue} will be generated instead.
 */
@Subscribable
@Description(value = "a raw function before refinement", broadcasted = BroadcastRequirement.REFINED_DECLARATION)
public final class RawFunction extends AbstractSyntax implements Named, Refinable {
    private String name;
    private String args;

    /**
     * Creates a new {@link RawFunction} instance.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param name
     *     The function name.
     * @param args
     *     The raw function args.
     */
    public RawFunction(int line, int column, String name, String args) {
        super(line, column);
        this.name = name;
        this.args = args;
        status(Status.RAW);
    }

    /**
     * Changes the name of the function.
     *
     * @param name
     *     The new function name.
     *
     * @return this, for chaining.
     */
    public RawFunction name(String name) {
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
     * Changes the raw function args.
     *
     * @param args
     *     The new function args.
     *
     * @return this, for chaining.
     */
    public RawFunction args(String args) {
        this.args = checkNotNull(args, "args cannot be null");
        return this;
    }

    /**
     * Gets the function args.
     *
     * @return The function args.
     */
    public String args() {
        return args;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(name).append('(').append(args).append(')');
    }

    @Override
    public RawFunction copy() {
        throw new UnsupportedOperationException("copy not supported for " + RawFunction.class);
    }

    @Override
    public boolean isRefined() {
        return false;
    }

    @Override
    public boolean shouldBreakBroadcast(SubscriptionPhase phase) {
        return status() == Status.PARSED || super.shouldBreakBroadcast(phase);
    }
}