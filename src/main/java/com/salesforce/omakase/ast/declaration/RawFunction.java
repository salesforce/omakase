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
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.refiner.StandardRefinerStrategy;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * Raw syntax representing a function.
 * <p/>
 * This is usually just an intermediary object passed to {@link Refiner#refine(RawFunction)}.
 *
 * @author nmcwilliams
 * @see StandardRefinerStrategy#refine(RawFunction, Broadcaster, Refiner)
 * @see GenericFunctionValue
 */

public final class RawFunction extends AbstractSyntax {
    private final String name;
    private final String args;

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
     * Gets the function name.
     *
     * @return The function name.
     */
    public String name() {
        return name;
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
        throw new UnsupportedOperationException();
    }
}
