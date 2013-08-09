/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.salesforce.omakase.ast.Linkable;

/**
 * TODO Description
 * 
 * XXX {@link ListIterator}?
 * 
 * @author nmcwilliams
 */
public class LinkableIterator<T extends Linkable<T>> implements Iterator<T> {
    private final T head;
    private T current;

    /**
     * TODO
     * 
     * @param head
     *            TODO
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
        throw new UnsupportedOperationException(); // figure this out later
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
    public static <T extends Linkable<T>> LinkableIterator<T> create(T head) {
        return new LinkableIterator<T>(head);
    }
}
