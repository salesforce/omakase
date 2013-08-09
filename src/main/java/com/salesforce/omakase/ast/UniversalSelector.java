/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

/**
 * Represents the universal selector, i.e., "*".
 * 
 * @author nmcwilliams
 */
public class UniversalSelector extends AbstractLinkableSyntax<SelectorPart> implements SelectorPart {
    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    public UniversalSelector(int line, int column) {
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
        return SelectorPartType.UNIVERSAL;
    }

    @Override
    protected SelectorPart self() {
        return this;
    }
}
