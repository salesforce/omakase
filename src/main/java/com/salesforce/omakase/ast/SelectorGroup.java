/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.google.common.base.Objects;
import com.salesforce.omakase.LinkableCollection;
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
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("selectors", head != null ? selectors() : head)
            .toString();
    }
}
