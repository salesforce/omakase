/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import com.salesforce.omakase.syntax.Selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class RefinedSelector extends BaseSyntaxUnit implements Selector {
    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    public RefinedSelector(int line, int column) {
        super(line, column);
        // TODO Auto-generated constructor stub
    }

    /**
     * TODO
     * 
     * @param rawSelectorGroup
     *            TODO
     */
    public RefinedSelector(RawSelector rawSelectorGroup) {
        super(rawSelectorGroup.line(), rawSelectorGroup.column());
        // TODO Auto-generated constructor stub
    }

    @Override
    public RefinedSelector refine() {
        return this;
    }

    @Override
    public String selector() {
        // TODO Auto-generated method stub
        return null;
    }

}
