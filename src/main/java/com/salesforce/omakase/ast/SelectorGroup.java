/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.LinkableCollection;
import com.salesforce.omakase.Util;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@Subscribable
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
    public LinkableCollection<Selector> selectors() {
        return LinkableCollection.of(head);
    }

    @Override
    public String toString() {
        return Util.toStringHelper(this)
            .add("line", line()).inline("column", column())
            .add("selectors", selectors())
            .indent()
            .toString();
    }
}
