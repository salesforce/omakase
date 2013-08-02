/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import java.util.List;

import com.salesforce.omakase.ast.Syntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
abstract class AbstractSyntax implements Syntax {
    private final int line;
    private final int column;

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
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
    public List<String> comments() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> ownComments() {
        // TODO Auto-generated method stub
        return null;
    }
}
