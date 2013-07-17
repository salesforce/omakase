/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.salesforce.omakase.syntax.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class RawSelectorGroup extends BaseSyntaxUnit implements SelectorGroup {
    private final String selectorGroup;

    /**
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param selectorGroup
     *            TODO
     */
    public RawSelectorGroup(int line, int column, String selectorGroup) {
        super(line, column);
        this.selectorGroup = checkNotNull(selectorGroup, "selectorGroup cannot be null");
    }

    @Override
    public RefinedSelectorGroup refine() {
        return new RefinedSelectorGroup(this);
    }

    @Override
    public String selectorGroup() {
        return selectorGroup;
    }

}
