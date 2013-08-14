/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.ast.AbstractLinkable;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * Represents the CSS universal selector, i.e., "*".
 * 
 * @author nmcwilliams
 */
@Subscribable
public class UniversalSelector extends AbstractLinkable<SelectorPart> implements SelectorPart {
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
        return SelectorPartType.UNIVERSAL_SELECTOR;
    }

    @Override
    protected SelectorPart self() {
        return this;
    }
}