/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_SELECTOR;

/**
 * TESTME
 * <p/>
 * Represents a CSS attribute selector.
 * <p/>
 * TODO unimplemented!
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "attribute selector segment", broadcasted = REFINED_SELECTOR)
public class AttributeSelector extends AbstractGroupable<SelectorPart> implements SimpleSelector {
    /**
     * Creates a new instance with the given line and column numbers.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     */
    public AttributeSelector(int line, int column) {
        super(line, column);
    }

    /** Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units). */
    public AttributeSelector() {
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
        return SelectorPartType.ATTRIBUTE_SELECTOR;
    }

    @Override
    protected SelectorPart self() {
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (isDetached()) return;
        // TODO Auto-generated method stub

    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("position", super.toString())
            .toString();
    }
}
