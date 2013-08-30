/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_SELECTOR;

import java.io.IOException;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.selector.CombinatorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * Represents a CSS selector part combinator.
 * 
 * @see CombinatorParser
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "combinator segment", broadcasted = REFINED_SELECTOR)
public class Combinator extends AbstractGroupable<SelectorPart> implements SelectorPart {
    private final CombinatorType type;

    /**
     * Creates a new instance with the given line and column numbers, and the {@link CombinatorType}.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     * @param type
     *            The {@link CombinatorType}.
     */
    public Combinator(int line, int column, CombinatorType type) {
        super(line, column);
        this.type = type;
    }

    @Override
    public boolean isSelector() {
        return false;
    }

    @Override
    public boolean isCombinator() {
        return true;
    }

    @Override
    public SelectorPartType type() {
        switch (type) {
        case DESCENDANT:
            return SelectorPartType.DESCENDANT_COMBINATOR;
        case CHILD:
            return SelectorPartType.CHILD_COMBINATOR;
        case ADJACENT_SIBLING:
            return SelectorPartType.ADJACENT_SIBLING_COMBINATOR;
        case GENERAL_SIBLING:
            return SelectorPartType.GENERAL_SIBLING_COMBINATOR;
        }
        throw new RuntimeException("unknown combinator type");
    }

    @Override
    protected SelectorPart self() {
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        switch (type) {
        case DESCENDANT:
            appendable.append(' ');
            break;
        case CHILD:
            appendable.spaceIf(writer.isVerbose());
            appendable.append('>');
            appendable.spaceIf(writer.isVerbose());
            break;
        case ADJACENT_SIBLING:
            appendable.spaceIf(writer.isVerbose());
            appendable.append('+');
            appendable.spaceIf(writer.isVerbose());
            break;
        case GENERAL_SIBLING:
            appendable.spaceIf(writer.isVerbose());
            appendable.append('~');
            appendable.spaceIf(writer.isVerbose());
            break;
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("position", super.toString())
            .add("type", type)
            .toString();
    }
}
