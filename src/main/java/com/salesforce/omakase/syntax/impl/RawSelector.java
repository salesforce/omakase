/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import com.google.common.base.Objects;
import com.salesforce.omakase.ast.selector.Selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class RawSelector extends AbstractSyntaxUnit implements Selector {
    private final String selector;

    /**
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param selector
     *            TODO
     */
    public RawSelector(int line, int column, String selector) {
        super(line, column);
        this.selector = selector;
    }

    @Override
    public RefinedSelector refine() {
        return new RefinedSelector(this);
    }

    @Override
    public String selector() {
        return selector;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("selector", selector)
            .add("line", line())
            .add("column", column())
            .toString();
    }

}
