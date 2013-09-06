/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_SELECTOR;

import java.io.IOException;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.selector.IdSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * TESTME Represents a CSS id selector.
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
     * TODO
     * 
     * @param name
     *            TODO
     */
    public IdSelector(String name) {
        name(name);
    }

    /**
     * Sets the id name.
     * 
     * @param name
     *            The id name.
     * @return this, for chaining.
     */
    public IdSelector name(String name) {
        this.name = checkNotNull(name, "name cannot be null");
        return this;
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
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append('#').append(name);
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("position", super.toString())
            .add("name", name)
            .toString();
    }
}
