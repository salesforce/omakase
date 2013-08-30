/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.As;

/**
 * TESTME Base class for {@link Syntax} units.
 * 
 * @author nmcwilliams
 */
public abstract class AbstractSyntax implements Syntax {
    private final int line;
    private final int column;

    /**
     * Creates a new instance with the given line and column numbers.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     */
    public AbstractSyntax(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int column() {
        return column;
    }

    @Override
    public String filterName() {
        return "";
    }

    @Override
    public String toString() {
        return As.stringNamed("")
            .add("line", line)
            .add("column", column)
            .toString();
    }
}
