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
public class SelectorGroup extends AbstractSyntax {
    private final Selector head;

    /**
     * TODO
     * 
     * @param head
     *            TOOD
     */
    public SelectorGroup(Selector head) {
        super(head.line(), head.column());
        this.head = head;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Selector first() {
        return head;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Selector last() {
        return head.tail();
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Iterator<Selector> selectors() {
        return LinkableIterator.create(head);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("selectors", head != null ? selectors() : head)
            .toString();
    }
}
