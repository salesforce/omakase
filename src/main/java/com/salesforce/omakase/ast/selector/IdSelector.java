/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.ast.AbstractLinkableSyntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class IdSelector extends AbstractLinkableSyntax<SelectorPart> implements SelectorPart {

    /**
     * @param line
     * @param column
     */
    protected IdSelector(int line, int column) {
        super(line, column);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected SelectorPart get() {
        return this;
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

}
