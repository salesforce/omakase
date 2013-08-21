/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.ast.collection.AbstractGroupable;

/**
 * Represents a CSS at-rule.
 * 
 * @author nmcwilliams
 */
public class AtRule extends AbstractGroupable<Statement> implements Statement {
    /**
     * Constructs a new {@link AtRule} instance.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     */
    public AtRule(int line, int column) {
        super(line, column);
    }

    @Override
    protected Statement self() {
        return this;
    }
}
