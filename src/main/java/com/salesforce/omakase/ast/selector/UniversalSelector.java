/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_SELECTOR;

import com.salesforce.omakase.ast.AbstractLinkable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * Represents the CSS universal selector, i.e., "*".
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "universal selector segment", broadcasted = REFINED_SELECTOR)
public class UniversalSelector extends AbstractLinkable<SelectorPart> implements SelectorPart {
    /**
     * Constructs a new {@link UniversalSelector} instance.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
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
