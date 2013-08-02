/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.selector.RefinedSelectorGroup;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * Standard implementation of a {@link SelectorGroup}.
 * 
 * <p> Not intended for subclassing or direct reference by clients.
 * 
 * @author nmcwilliams
 */
final class StandardSelectorGroup extends AbstractSyntax implements RefinedSelectorGroup {
    private final String content;
    private ImmutableList<Selector> selectors;

    StandardSelectorGroup(int line, int column, String content) {
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
        return selectors;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("content", content.replaceAll("\n", "↳"))
            .add("selectors", selectors)
            .toString();
    }
}
