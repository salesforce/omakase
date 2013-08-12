/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.Iterator;

import com.salesforce.omakase.As;
import com.salesforce.omakase.LinkableCollection;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * Represents a CSS selector group.
 * 
 * <p>
 * A selector group is a list of {@link Selector}s, with each {@link Selector} separated by a comma. For example, in
 * 
 * <pre>.class #id, #id { ... } </pre>
 * 
 * The selector group is
 * 
 * <pre>.class #id, #id</pre>
 * 
 * <p>
 * Each {@link Rule} has one and only one {@link SelectorGroup}.
 * 
 * @author nmcwilliams
 */
@Subscribable
public class SelectorGroup extends AbstractSyntax implements Iterable<Selector> {
    private final Selector head;

    /**
     * Creates a new instance with the given {@link Selector} at the beginning.
     * 
     * @param head
     *            The first {@link Selector} in the {@link SelectorGroup}.
     */
    public SelectorGroup(Selector head) {
        super(head.line(), head.column());
        this.head = head;
    }

    /**
     * Gets the first {@link Selector} in the group.
     * 
     * @return The first {@link Selector}.
     */
    public Selector first() {
        return head;
    }

    /**
     * Gets the last (or <em>key</em>) {@link Selector} in the group.
     * 
     * @return The last {@link Selector}.
     */
    public Selector last() {
        return head.tail();
    }

    /**
     * Gets a {@link LinkableCollection} of each {@link Selector} in this group.
     * 
     * @return The {@link LinkableCollection} of selectors.
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
