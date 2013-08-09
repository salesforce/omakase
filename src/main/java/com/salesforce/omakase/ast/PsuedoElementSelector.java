/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.google.common.base.Objects;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class PsuedoElementSelector extends AbstractLinkableSyntax<SelectorPart> implements SelectorPart {

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    protected PsuedoElementSelector(int line, int column) {
        super(line, column);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean isSelector() {
        return true;
    }

    @Override
    public boolean isCombinator() {
        return false;
    }

    @Override
    public SelectorPartType type() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected PsuedoElementSelector get() {
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .toString();
    }
}
