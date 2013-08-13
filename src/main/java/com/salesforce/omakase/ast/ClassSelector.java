/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.As;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * Represents a CSS class selector.
 * 
 * @author nmcwilliams
 */
@Subscribable
public class ClassSelector extends AbstractLinkableSyntax<SelectorPart> implements SelectorPart {
    private String name;

    /**
     * Creates a new instance with the given line and column numbers.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     * @param name
     *            The name of the class.
     */
    public ClassSelector(int line, int column, String name) {
        super(line, column);
        this.name = name;
    }

    /**
     * Gets the class name.
     * 
     * @return The class name.
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
        return SelectorPartType.CLASS;
    }

    @Override
    protected SelectorPart self() {
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
