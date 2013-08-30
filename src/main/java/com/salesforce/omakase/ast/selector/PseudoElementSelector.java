/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_SELECTOR;

import java.io.IOException;
import java.util.Set;

import com.google.common.collect.Sets;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.selector.PseudoSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * TESTME Represents a CSS pseudo element selector.
 * 
 * @see PseudoSelectorParser
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "pseudo element selector segment", broadcasted = REFINED_SELECTOR)
public class PseudoElementSelector extends AbstractGroupable<SelectorPart> implements SelectorPart {
    /** these can use pseudo class syntax but are actually pseudo elements */
    public static final Set<String> POSERS = Sets.newHashSet("first-line", "first-letter", "before", "after");

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
        this.name = name.toLowerCase();
    }

    /**
     * Sets the name of the selector.
     * 
     * @param name
     *            The new name.
     * @return this, for chaining.
     */
    public PseudoElementSelector name(String name) {
        checkNotNull(name, "name cannot be null");
        this.name = name.toLowerCase();
        return this;
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
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        // TODO function args
        appendable.append(POSERS.contains(name) ? ":" : "::").append(name);
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
