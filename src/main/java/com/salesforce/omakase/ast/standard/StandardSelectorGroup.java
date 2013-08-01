/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.selector.RefinedSelectorGroup;
import com.salesforce.omakase.ast.selector.Selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
final class StandardSelectorGroup extends AbstractSyntax implements RefinedSelectorGroup {
    private final String content;
    private ImmutableList<Selector> selectors;

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param content
     *            TODO
     */
    public StandardSelectorGroup(int line, int column, String content) {
        super(line, column);
        this.content = content;
        this.selectors = ImmutableList.of();
    }

    @Override
    public RefinedSelectorGroup refine() {
        if (selectors.isEmpty()) {
            // TODO, refinement
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
            .add("raw", content)
            .toString();
    }
}
