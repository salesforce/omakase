/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_SELECTOR;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.AbstractLinkable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * Represents a CSS pseudo class selector.
 * 
 * <p>
 * Note that even though some pseudo elements can be written using the pseudo class format, they are <b>not</b>
 * considered pseudo classes in this library, but as {@link PseudoElementSelector}s.
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "pseudo class selector segment", broadcasted = REFINED_SELECTOR)
public class PseudoClassSelector extends AbstractLinkable<SelectorPart> implements SimpleSelector {
    private String name;

    /**
     * Constructs a new {@link PseudoClassSelector} instance with the given name.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     * @param name
     *            Name of the pseudo class.
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
            .indent()
            .add("syntax", super.toString())
            .add("name", name)
            .toString();
    }
}
