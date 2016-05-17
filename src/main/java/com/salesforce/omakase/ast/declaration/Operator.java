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

import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * An {@link OperatorType} in between a {@link Term}, as property of the {@link PropertyValue} {@link PropertyValue}.
 *
 * @author nmcwilliams
 */
public final class Operator extends AbstractGroupable<PropertyValue, PropertyValueMember> implements PropertyValueMember {
    private final OperatorType type;

    /**
     * Constructs a new {@link Operator} instance (used for dynamically created {@link Syntax} units).
     * <p>
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
     * <p>
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
    public boolean isTerm() {
        return false;
    }

    @Override
    protected Operator self() {
        return this;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.spaceIf(writer.isVerbose() && type == OperatorType.SLASH);
        type.write(writer, appendable);
        appendable.spaceIf(writer.isVerbose() && type != OperatorType.SPACE);
    }

    @Override
    public Operator copy() {
        return new Operator(type).copiedFrom(this);
    }
}
