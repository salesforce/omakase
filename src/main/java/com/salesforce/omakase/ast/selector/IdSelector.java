/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_SELECTOR;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.selector.IdSelectorParser;

/**
 * Represents a CSS ID selector.
 * 
 * @see IdSelectorParser
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "id selector segment", broadcasted = REFINED_SELECTOR)
public class IdSelector extends AbstractGroupable<SelectorPart> implements SimpleSelector {
    private String name;

    /**
     * Creates a new instance with the given line and column numbers and id name.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     * @param name
     *            Content of the selector.
     */
    public IdSelector(int line, int column, String name) {
        super(line, column);
        this.name = name;
    }

    /**
     * Gets the id name.
     * 
     * @return The id name.
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
        return SelectorPartType.ID_SELECTOR;
    }

    @Override
    protected SelectorPart self() {
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
