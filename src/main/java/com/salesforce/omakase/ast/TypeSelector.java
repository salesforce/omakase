/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.As;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * Represents a CSS type selector (also known as an element type selector).
 * 
 * @author nmcwilliams
 */
@Subscribable
public class TypeSelector extends AbstractLinkableSyntax<SelectorPart> implements SimpleSelector {
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
    public TypeSelector(int line, int column, String name) {
        super(line, column);
        this.name = name;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public String name() {
        return name;
    }

    @Override
    public String filterName() {
        return name;
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
        return SelectorPartType.TYPE_SELECTOR;
    }

    @Override
    protected SelectorPart self() {
        return this;
    }

    @Override
    public String toString() {
        return As.string(this).add("syntax", super.toString()).add("name", name).toString();
    }
}
