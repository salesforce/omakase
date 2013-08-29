/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;


import java.io.IOException;

import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

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
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        // TODO Auto-generated method stub
    }

    @Override
    protected Statement self() {
        return this;
    }
}
