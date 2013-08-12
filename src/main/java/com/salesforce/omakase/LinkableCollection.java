/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.Iterator;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Linkable;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 * @param <T>
 *            TODO
 */
public final class LinkableCollection<T extends Linkable<T>> implements Iterable<T> {
    private final Iterator<T> iterator;

    /**
     * TODO
     * 
     * @param head
     *            TODO
     */
    public LinkableCollection(T head) {
        this.iterator = (head == null) ? Iterators.<T>emptyIterator() : LinkableIterator.create(head);
    }

    @Override
    public Iterator<T> iterator() {
        return iterator;
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

    @Override
    public String toString() {
        return As.string(this).indent().add("items", Lists.newArrayList(this)).toString();
    }
}
