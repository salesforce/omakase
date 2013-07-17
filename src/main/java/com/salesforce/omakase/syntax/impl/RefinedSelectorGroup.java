/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import com.salesforce.omakase.syntax.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class RefinedSelectorGroup extends BaseSyntaxUnit implements SelectorGroup {
    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    public RefinedSelectorGroup(int line, int column) {
        super(line, column);
        // TODO Auto-generated constructor stub
    }

    /**
     * TODO
     * 
     * @param rawSelectorGroup
     *            TODO
     */
    public RefinedSelectorGroup(RawSelectorGroup rawSelectorGroup) {
        super(rawSelectorGroup.getLine(), rawSelectorGroup.getColumn());
        // TODO Auto-generated constructor stub
    }

    @Override
    public RefinedSelectorGroup refine() {
        return this;
    }

    @Override
    public String selectorGroup() {
        // TODO Auto-generated method stub
        return null;
    }

}
