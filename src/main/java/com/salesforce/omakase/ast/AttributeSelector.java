/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.google.common.base.Objects;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@Subscribable
public class AttributeSelector extends AbstractLinkableSyntax<SelectorPart> implements SelectorPart {
    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    protected AttributeSelector(int line, int column) {
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
        return SelectorPartType.ATTRIBUTE;
    }

    @Override
    protected SelectorPart self() {
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .toString();
    }
}
