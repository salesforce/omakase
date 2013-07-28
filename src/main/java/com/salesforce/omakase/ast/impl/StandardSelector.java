/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.impl;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.RefinedSelector;
import com.salesforce.omakase.ast.SelectorPart;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
final class StandardSelector extends AbstractSyntax implements RefinedSelector {
    private List<SelectorPart> parts;
    private String raw;

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param raw
     *            TODO
     */
    public StandardSelector(int line, int column, String raw) {
        super(line, column);
        this.raw = raw;
    }

    @Override
    public RefinedSelector refine() {
        if (dirty()) {
            // TODO parse parts
            System.out.println(raw);
            parts = Lists.newArrayList();
            dirty(false);
        }

        return this;
    }

    @Override
    public List<SelectorPart> parts() {
        return ImmutableList.copyOf(parts);
    }

    @Override
    public <T extends SelectorPart> List<T> parts(Class<T> type) {
        // TODO Auto-generated method stub
        return null;
    }
}
