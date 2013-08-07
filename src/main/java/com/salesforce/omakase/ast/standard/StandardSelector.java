/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.RefinedSelector;
import com.salesforce.omakase.ast.Selector;
import com.salesforce.omakase.ast.SelectorPart;

/**
 * Standard implementation of a {@link Selector}.
 * 
 * <p> Not intended for subclassing or direct reference by clients.
 * 
 * @author nmcwilliams
 */
final class StandardSelector extends AbstractLinkableSyntax<Selector> implements RefinedSelector {
    private final String original;
    private List<SelectorPart> parts;

    StandardSelector(int line, int column, String original) {
        super(line, column);
        this.original = original;
        this.parts = ImmutableList.of();
    }

    @Override
    public RefinedSelector refine() {
        if (parts.isEmpty()) {
            // TODO
        }

        return this;
    }

    @Override
    public String original() {
        return original;
    }

    @Override
    public RefinedSelector part(SelectorPart part) {
        this.parts.add(part);
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

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("content", original)
            .add("parts", parts)
            .toString();
    }

}
