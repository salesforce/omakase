/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import com.google.common.base.Objects;
import com.salesforce.omakase.ast.AbstractLinkable;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@Subscribable
public class PseudoElementSelector extends AbstractLinkable<SelectorPart> implements SelectorPart {
    /**
     * Constructs a new {@link PseudoElementSelector} selector with the given name.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     */
    protected PseudoElementSelector(int line, int column) {
        super(line, column);
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
        return SelectorPartType.PSEUDO_ELEMENT_SELECTOR;
    }

    @Override
    protected PseudoElementSelector self() {
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .toString();
    }
}
