/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import java.util.Iterator;

import com.salesforce.omakase.LinkableIterator;
import com.salesforce.omakase.ast.AbstractLinkableSyntax;
import com.salesforce.omakase.ast.Refinable;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class Selector extends AbstractLinkableSyntax<Selector> implements Refinable<RefinedSelector>, RefinedSelector {
    private final String raw;
    private SelectorPart head;

    /**
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    protected Selector(int line, int column, String raw) {
        super(line, column);
        this.raw = raw;
    }

    @Override
    protected Selector get() {
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public String raw() {
        return raw;
    }

    @Override
    public RefinedSelector refine() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<SelectorPart> parts() {
        return LinkableIterator.create(head);
    }

}
