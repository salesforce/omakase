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
public class ClassSelector extends AbstractLinkableSyntax<SelectorPart> implements SelectorPart {

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    protected ClassSelector(int line, int column) {
        super(line, column);
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
