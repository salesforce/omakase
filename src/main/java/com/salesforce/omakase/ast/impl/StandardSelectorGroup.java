/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.impl;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.RefinedSelectorGroup;
import com.salesforce.omakase.ast.Selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StandardSelectorGroup extends AbstractSyntax implements RefinedSelectorGroup {
    private List<Selector> selectors;
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
    public StandardSelectorGroup(int line, int column, String raw) {
        super(line, column);
        this.raw = raw;
    }

    @Override
    public RefinedSelectorGroup refine() {
        if (dirty()) {
            // TODO, refinement
            dirty(false);
        }

        return this;
    }

    @Override
    public List<Selector> selectors() {
        return ImmutableList.copyOf(selectors);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("selectors", selectors)
            .add("raw", raw)
            .toString();
    }
}
