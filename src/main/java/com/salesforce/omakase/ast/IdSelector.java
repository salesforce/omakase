/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.google.common.base.Objects;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class IdSelector extends AbstractLinkableSyntax<SelectorPart> implements SelectorPart {
    private String name;

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param name
     *            TODO
     */
    protected IdSelector(int line, int column, String name) {
        super(line, column);
        this.name = name;
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
        return SelectorPartType.ID;
    }

    @Override
    public String filterName() {
        return name;
    }

    @Override
    protected SelectorPart get() {
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("name", name)
            .toString();
    }
}
