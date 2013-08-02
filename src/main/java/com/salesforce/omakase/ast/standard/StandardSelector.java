/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.selector.RefinedSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;

/**
 * Standard implementation of a {@link Selector}.
 * 
 * <p> Not intended for subclassing or direct reference by clients.
 * 
 * @author nmcwilliams
 */
final class StandardSelector extends AbstractSyntax implements RefinedSelector {
    private final String content;
    private ImmutableList<SelectorPart> parts;

    StandardSelector(int line, int column, String content) {
        super(line, column);
        this.content = content;
        this.parts = ImmutableList.of();
    }

    @Override
    public RefinedSelector refine() {
        if (parts.isEmpty()) {
            // TODO parse parts
        }

        return this;
    }

    @Override
    public String content() {
        return content;
    }

    @Override
    public List<SelectorPart> parts() {
        return parts;
    }

    @Override
    public <T extends SelectorPart> List<T> parts(Class<T> type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("content", content)
            .add("parts", parts)
            .toString();
    }
}
