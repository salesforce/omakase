/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import java.util.List;

import com.salesforce.omakase.ast.Linkable;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 * @param <T>
 *            TODO
 */
public abstract class AbstractLinkableSyntax<T> extends AbstractSyntax implements Linkable<T> {
    /**
     * Creates a new instance with the given line and column numbers.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     */
    protected AbstractLinkableSyntax(int line, int column) {
        super(line, column);
    }

    @Override
    public Linkable<T> previous(T previous) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public T previous() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Linkable<T> next(T next) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public T next() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<T> group() {
        // TODO Auto-generated method stub
        return null;
    }
}
