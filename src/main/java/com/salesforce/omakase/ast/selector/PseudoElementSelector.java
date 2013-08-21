/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_SELECTOR;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * Represents a CSS pseudo element selector.
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "pseudo element selector segment", broadcasted = REFINED_SELECTOR)
public class PseudoElementSelector extends AbstractGroupable<SelectorPart> implements SelectorPart {
    private String name;

    /**
     * Constructs a new {@link PseudoElementSelector} selector with the given name.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     * @param name
     *            Name of the pseudo element.
     */
    public PseudoElementSelector(int line, int column, String name) {
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
        return SelectorPartType.PSEUDO_ELEMENT_SELECTOR;
    }

    @Override
    protected PseudoElementSelector self() {
        return this;
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("syntax", super.toString())
            .add("name", name)
            .toString();
    }
}
