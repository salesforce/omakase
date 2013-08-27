/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.collection;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.Syntax;

/**
 * Standard (default) implementation of the {@link SyntaxCollection}.
 * 
 * @param <T>
 *            Type of items in the {@link SyntaxCollection}.
 * 
 * @author nmcwilliams
 */
public class StandardSyntaxCollection<T extends Syntax & Groupable<T>> implements SyntaxCollection<T> {
    private final LinkedList<T> list = Lists.newLinkedList();

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(T unit) {
        return list.contains(unit);
    }

    @Override
    public Optional<T> first() {
        return list.isEmpty() ? Optional.<T>absent() : Optional.of(list.peekFirst());
    }

    @Override
    public Optional<T> last() {
        return list.isEmpty() ? Optional.<T>absent() : Optional.of(list.peekLast());
    }

    @Override
    public SyntaxCollection<T> prepend(T unit) {
        checkNotNull(unit, "unit cannot be null");
        list.push(unit);
        return this;
    }

    @Override
    public SyntaxCollection<T> prependAll(Iterable<T> units) {
        list.addAll(0, Lists.newArrayList(units));
        return this;
    }

    @Override
    public SyntaxCollection<T> prependBefore(T existing, T unit) throws IllegalArgumentException {
        checkNotNull(existing, "exiting cannot be null");
        checkNotNull(unit, "unit cannot be null");

        int index = list.indexOf(existing);

        if (index == -1) throw new IllegalArgumentException("the specified unit does not exist in this collection!");

        list.add(index, unit);

        return this;
    }

    @Override
    public SyntaxCollection<T> append(T unit) {
        checkNotNull(unit, "unit cannot be null");
        list.add(unit);
        unit.parent(this);
        return this;
    }

    @Override
    public SyntaxCollection<T> appendAll(Iterable<T> units) {
        list.addAll(Lists.newArrayList(units));
        return this;
    }

    @Override
    public SyntaxCollection<T> appendAfter(T existing, T unit) throws IllegalArgumentException {
        checkNotNull(existing, "exiting cannot be null");
        checkNotNull(unit, "unit cannot be null");

        int index = list.indexOf(existing);

        if (index == -1) throw new IllegalArgumentException("the specified unit does not exist in this collection!");

        if (index == (list.size() - 1)) {
            list.add(unit);
        } else {
            list.add(index + 1, unit);
        }

        return this;
    }

    @Override
    public SyntaxCollection<T> replaceExistingWith(Iterable<T> units) {
        clear();
        appendAll(units);
        return this;
    }

    @Override
    public SyntaxCollection<T> detach(T unit) {
        list.remove(unit);
        unit.parent(null);
        return this;
    }

    @Override
    public Iterable<T> clear() {
        List<T> detached = ImmutableList.copyOf(list);

        for (T unit : list) {
            detach(unit);
        }

        return detached;
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("items", list)
            .toString();
    }

    /**
     * Creates a new {@link SyntaxCollection} instance.
     * 
     * @param <E>
     *            Type of items the collection contains.
     * 
     * @return The new {@link SyntaxCollection} instance.
     */
    public static <E extends Syntax & Groupable<E>> SyntaxCollection<E> create() {
        return new StandardSyntaxCollection<>();
    }

}
