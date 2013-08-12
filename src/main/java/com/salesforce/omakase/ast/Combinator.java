/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.As;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * Represents a CSS selector part combinator.
 * 
 * @author nmcwilliams
 */
@Subscribable
public class Combinator extends AbstractLinkableSyntax<SelectorPart> implements SelectorPart {
    private final CombinatorType type;

    /**
     * Creates a new instance with the given line and column numbers, and the {@link CombinatorType}.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     * @param type
     *            The {@link CombinatorType}.
     */
    public Combinator(int line, int column, CombinatorType type) {
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
            return SelectorPartType.CHILD;
        case DESCENDANT:
            return SelectorPartType.DESCENDENT;
        case GENERAL:
            return SelectorPartType.GENERAL_SIBLING;
        }
        throw new RuntimeException("unknown combinator type");
    }

    @Override
    protected SelectorPart self() {
        return this;
    }

    @Override
    public String toString() {
        return As.string(this).add("syntax", super.toString()).add("type", type.symbol()).toString();

    }
}
