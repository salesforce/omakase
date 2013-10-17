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

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * An {@link OperatorType} in between a {@link Term}, as property of the {@link TermList} {@link PropertyValue}.
 *
 * @author nmcwilliams
 */
public final class Operator extends AbstractGroupable<TermList, TermListMember> implements TermListMember {
    private final OperatorType type;

    /**
     * Constructs a new {@link Operator} instance (used for dynamically created {@link Syntax} units).
     * <p/>
     * This should be used for dynamically created units.
     *
     * @param type
     *     The type of operator.
     */
    public Operator(OperatorType type) {
        this(-1, -1, type);
    }

    /**
     * Constructs a new {@link Operator} instance.
     * <p/>
     * If dynamically creating a new instance then use {@link #Operator(OperatorType)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param type
     *     The type of operator.
     */
    public Operator(int line, int column, OperatorType type) {
        super(line, column);
        this.type = type;
        status(Status.NEVER_EMIT);
    }

    /**
     * Gets the {@link OperatorType}.
     *
     * @return The {@link OperatorType}.
     */
    public OperatorType type() {
        return type;
    }

    @Override
    protected TermListMember self() {
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        type.write(writer, appendable);
    }

    @Override
    public String toString() {
        return As.string(this).add("type", type).toString();
    }
}
