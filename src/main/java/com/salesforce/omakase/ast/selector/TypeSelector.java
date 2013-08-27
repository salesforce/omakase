/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import static com.salesforce.omakase.emitter.EmittableRequirement.REFINED_SELECTOR;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Emittable;

/**
 * Represents a CSS type selector (also known as an element type selector).
 * 
 * @author nmcwilliams
 */
@Emittable
@Description(value = "type/element selector segment", broadcasted = REFINED_SELECTOR)
public class TypeSelector extends AbstractGroupable<SelectorPart> implements SimpleSelector {
    private String name;

    /**
     * Constructs a new {@link TypeSelector} instance with the given name.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     * @param name
     *            Name of the element / type.
     */
    public TypeSelector(int line, int column, String name) {
        super(line, column);
        this.name = name;
    }

    /**
     * Gets the name of the selector.
     * 
     * @return The name.
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
        return As.string(this)
            .indent()
            .add("syntax", super.toString())
            .add("name", name)
            .toString();
    }
}
