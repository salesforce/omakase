/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import java.util.Iterator;

import com.salesforce.omakase.LinkableIterator;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Refinable;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class SelectorGroup extends AbstractSyntax implements Refinable<SelectorGroup>, RefinedSelectorGroup {
    private final String raw;
    private Selector head;

    /**
     * @param line
     * @param column
     * @param raw
     */
    public SelectorGroup(int line, int column, String raw) {
        super(line, column);
        this.raw = raw;
    }

    @Override
    public SelectorGroup refine() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<Selector> selectors() {
        return LinkableIterator.create(head);
    }
}
