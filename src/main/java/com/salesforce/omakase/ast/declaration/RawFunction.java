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

import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.broadcast.BroadcastRequirement;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.refiner.FunctionRefiner;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Raw syntax representing a function.
 * <p/>
 * This is usually just an intermediary object passed to {@link MasterRefiner#refine(RawFunction)}. You can subscribe to this
 * method if you would like to check ALL function-like values (e.g., to modify the raw args) before they are refined into the more
 * specifically-typed function value.
 *
 * @author nmcwilliams
 * @see FunctionRefiner
 * @see GenericFunctionValue
 */
@Subscribable
@Description(value = "a raw function before refinement", broadcasted = BroadcastRequirement.REFINED_DECLARATION)
public final class RawFunction extends AbstractSyntax {
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
}
