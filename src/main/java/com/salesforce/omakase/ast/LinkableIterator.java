/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;


/**
 * An iterator over {@link Linkable} objects.
 * 
 * XXX {@link ListIterator}?
 * 
 * @see LinkableCollection
 * 
 * @param <T>
 *            Type of items the {@link Linkable}s contain.
 * @author nmcwilliams
 */
public class LinkableIterator<T extends Linkable<T>> implements Iterator<T> {
    private final T head;
    private T current;

    /**
     * Constructs a new {@link LinkableIterator} instances with the given head.
     * 
     * @param head
     *            The first element in the collection
     */
    public LinkableIterator(T head) {
        this.head = checkNotNull(head, "head cannot be null");
    }

    @Override
    public boolean hasNext() {
        if (current == null) return true;
        return !current.isTail();
    }

    @Override
    public T next() {
        if (current == null) {
            current = head;
        } else if (!hasNext()) {
            throw new NoSuchElementException();
        } else {
            current = current.next().get();
        }

        return current;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(); // can implement later if needed
    }

    /**
     * Constructor method to create a new instance {@link LinkableIterator} instance.
     * 
     * @param <T>
     *            Type of items the {@link Linkable}s contain.
     * @param head
     *            The first element in the collection.
     * @return The new {@link LinkableIterator} instance.
     */
    public static <T extends Linkable<T>> LinkableIterator<T> create(T head) {
        return new LinkableIterator<T>(head);
    }
}
