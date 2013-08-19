/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.salesforce.omakase.As;

/**
 * A "collection" of {@link Linkable} items. This doesn't actually hold a collection of anything, nor does it implement
 * the {@link Collection} interface. It only exists to satisfy APIs that require {@link Iterable}s.
 * 
 * @param <T>
 *            Type of items the {@link Linkable}s contain.
 * @author nmcwilliams
 */
public final class LinkableCollection<T extends Linkable<T>> implements Iterable<T> {
    private final Iterator<T> iterator;

    /**
     * Creates a new {@link LinkableCollection} instance with the given head element.
     * 
     * @param head
     *            The first element in the collection.
     */
    public LinkableCollection(T head) {
        this.iterator = (head == null) ? Iterators.<T>emptyIterator() : LinkableIterator.create(head);
    }

    @Override
    public Iterator<T> iterator() {
        return iterator;
    }

    /**
     * Constructor method to create a new {@link LinkableCollection} instance with the given head element.
     * 
     * @param <T>
     *            Type of items the {@link Linkable}s contain.
     * @param head
     *            The first element in the collection.
     * @return The new {@link LinkableCollection} instance.
     */
    public static <T extends Linkable<T>> LinkableCollection<T> of(T head) {
        return new LinkableCollection<T>(head);
    }

    @Override
    public String toString() {
        return As.string(this).indent().add("items", Lists.newArrayList(this)).toString();
    }
}
