/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.salesforce.omakase.syntax.Selector;
import com.google.common.base.Objects;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class RawSelector extends BaseSyntaxUnit implements Selector {
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
        this.selector = checkNotNull(selector, "selector cannot be null");
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
