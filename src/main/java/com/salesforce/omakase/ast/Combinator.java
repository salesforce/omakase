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
public class Combinator extends AbstractLinkableSyntax<SelectorPart> implements SelectorPart {
    private final CombinatorType type;

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param type
     *            TODO
     */
    protected Combinator(int line, int column, CombinatorType type) {
        super(line, column);
        this.type = type;
    }

    @Override
    public boolean isSelector() {
        return false;
    }

    @Override
    public boolean isCombinator() {
        return true;
    }

    @Override
    public SelectorPartType type() {
        switch (type) {
        case ADJACENT:
            return SelectorPartType.ADJACENT_SIBLING;
        case CHILD:
            return SelectorPartType.ADJACENT_SIBLING;
        case DESCENDENT:
            return SelectorPartType.ADJACENT_SIBLING;
        case GENERAL:
            return SelectorPartType.ADJACENT_SIBLING;
        }
        throw new RuntimeException("unknown combinator type");
    }

    @Override
    protected SelectorPart self() {
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("type", type.symbol())
            .toString();
    }
}
