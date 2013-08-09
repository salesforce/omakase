/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.Iterator;

import com.google.common.base.Objects;
import com.salesforce.omakase.LinkableIterator;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class Selector extends AbstractLinkableSyntax<Selector> implements Refinable<RefinedSelector>, RefinedSelector {
    private final RawSyntax rawContent;
    private SelectorPart head;

    /**
     * @param rawContent
     *            TODO
     */
    protected Selector(RawSyntax rawContent) {
        super(rawContent.line(), rawContent.column());
        this.rawContent = rawContent;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public RawSyntax rawContent() {
        return rawContent;
    }

    @Override
    public Iterator<SelectorPart> parts() {
        return LinkableIterator.create(head);
    }

    @Override
    public RefinedSelector refine() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Selector get() {
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("rawContent", rawContent)
            .add("selectorParts", parts())
            .toString();
    }
}
