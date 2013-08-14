/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.As;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@Subscribable
public class PseudoClassSelector extends AbstractLinkableSyntax<SelectorPart> implements SimpleSelector {
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
    public PseudoClassSelector(int line, int column, String name) {
        super(line, column);
        this.name = name;
    }

    /**
     * Gets the selector name (e.g., "hover").
     * 
     * @return The selector name.
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
        return SelectorPartType.PSEUDO_CLASS_SELECTOR;
    }

    @Override
    protected PseudoClassSelector self() {
        return this;
    }

    @Override
    public String toString() {
        return As.string(this)
            .add("syntax", super.toString())
            .add("name", name)
            .toString();
    }
}
