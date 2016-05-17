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

package com.salesforce.omakase.ast.atrule;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * A generic {@link AtRuleExpression} value.
 * <p>
 * This is used for refined {@link AtRule}s (standard or custom) that contain a simple expression.
 *
 * @author nmcwilliams
 */
public class GenericAtRuleExpression extends AbstractAtRuleMember implements AtRuleExpression {
    private String expression;

    /**
     * Creates anew instance with the given line and column numbers and simple expression.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param expression
     *     The  expression.
     */
    public GenericAtRuleExpression(int line, int column, String expression) {
        super(line, column);
        this.expression = expression;
    }

    /**
     * Creates a new instance with the given simple expression (used for dynamically created {@link Syntax} units).
     *
     * @param expression
     *     The  expression.
     */
    public GenericAtRuleExpression(String expression) {
        this.expression = expression;
    }

    /**
     * Sets the expression.
     *
     * @param expression
     *     The new expression.
     *
     * @return this, for chaining.
     */
    public GenericAtRuleExpression expression(String expression) {
        this.expression = expression;
        return this;
    }

    /**
     * Gets the expression.
     *
     * @return The expression.
     */
    public String expression() {
        return expression;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(expression);
    }

    @Override
    public GenericAtRuleExpression copy() {
        return new GenericAtRuleExpression(expression).copiedFrom(this);
    }
}
