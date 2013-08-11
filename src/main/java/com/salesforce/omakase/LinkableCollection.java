/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import com.salesforce.omakase.ast.Linkable;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 * @param <T>
 *            TODO
 */
public class LinkableCollection<T extends Linkable<T>> implements Iterable<T> {
    private final T head;

    /**
     * TODO
     * 
     * @param head
     *            TODO
     */
    public LinkableCollection(T head) {
        this.head = checkNotNull(head, "head cannot be null");
    }

    @Override
    public Iterator<T> iterator() {
        return LinkableIterator.create(head);
    }

    /**
     * TODO Description
     * 
     * @param <T>
     *            TODO
     * @param head
     *            TODO
     * @return TODO
     */
    public static <T extends Linkable<T>> LinkableCollection<T> of(T head) {
        return new LinkableCollection<T>(head);
    }
}
