/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.List;

import com.salesforce.omakase.As;

/**
 * Base class for {@link Syntax} units.
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
    protected AbstractSyntax(int line, int column) {
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
    public Syntax comment(String comment) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> comments() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> ownComments() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return As.named("AbstractSyntax")
            .add("line", line)
            .add("column", column)
            .add("comments", comments())
            .toString();
    }
}
