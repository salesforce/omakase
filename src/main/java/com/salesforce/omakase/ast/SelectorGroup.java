/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.Iterator;

import com.salesforce.omakase.As;
import com.salesforce.omakase.LinkableCollection;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@Subscribable
public class SelectorGroup extends AbstractSyntax implements Iterable<Selector> {
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
    public LinkableCollection<Selector> selectors() {
        return LinkableCollection.of(head);
    }

    @Override
    public Iterator<Selector> iterator() {
        return selectors().iterator();
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("syntax", super.toString())
            .add("selectors", selectors())
            .toString();
    }

}
