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

package com.salesforce.omakase.ast.atrule;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * A generic {@link AtRuleExpression} value.
 * <p/>
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
