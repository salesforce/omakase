/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.collection;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcaster.Broadcaster;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TESTME Standard (default) implementation of the {@link SyntaxCollection}.
 *
 * @param <T>
 *            Type of items in the {@link SyntaxCollection}.
 *
 * @author nmcwilliams
 */
public class StandardSyntaxCollection<T extends Syntax & Groupable<T>> implements SyntaxCollection<T> {
    private final LinkedList<T> list = Lists.newLinkedList();
    private Optional<Broadcaster> broadcaster;

    /**
     * TODO
     */
    public StandardSyntaxCollection() {
        this(null);
    }

    /**
     * TODO
     *
     * @param broadcaster
     *            TODO
     */
    public StandardSyntaxCollection(Broadcaster broadcaster) {
        this.broadcaster = Optional.fromNullable(broadcaster);
    }

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
    public boolean isEmptyOrAllDetached() {
        for (T unit : list) {
            if (!unit.isDetached()) return false;
        }

        return true;
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

        if (broadcaster.isPresent() && unit.broadcaster() == null) {
            unit.broadcaster(broadcaster.get());
        }

        list.push(unit);
        unit.parent(this);

        if (broadcaster.isPresent()) {
            unit.propagateBroadcast(broadcaster.get());
        }

        broadcast(unit);

        return this;
    }

    @Override
    public SyntaxCollection<T> prependAll(Iterable<T> units) {
        for (T unit : units) {
            prepend(unit);
        }
        return this;
    }

    @Override
    public SyntaxCollection<T> prependBefore(T existing, T unit) throws IllegalArgumentException {
        checkNotNull(existing, "exiting cannot be null");
        checkNotNull(unit, "unit cannot be null");

        int index = list.indexOf(existing);
        if (index == -1) throw new IllegalArgumentException("the specified unit does not exist in this collection!");

        if (broadcaster.isPresent() && unit.broadcaster() == null) {
            unit.broadcaster(broadcaster.get());
        }

        list.add(index, unit);
        unit.parent(this);

        broadcast(unit);

        return this;
    }

    @Override
    public SyntaxCollection<T> append(T unit) {
        checkNotNull(unit, "unit cannot be null");

        if (broadcaster.isPresent() && unit.broadcaster() == null) {
            unit.broadcaster(broadcaster.get());
        }

        list.add(unit);
        unit.parent(this);

        broadcast(unit);

        return this;
    }

    @Override
    public SyntaxCollection<T> appendAll(Iterable<T> units) {
        for (T unit : units) {
            append(unit);
        }
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

        broadcast(unit);

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

    /**
     * Internal method to broadcast new units.
     *
     * @param unit
     *            The unit to broadcast.
     */
    private void broadcast(T unit) {
        // propagation only happens for unbroadcasted units. Once a unit has been broadcasted it should take care of
        // ensuring any new broadcastable members are properly broadcasted when added.
        if (broadcaster.isPresent() && unit.status() == Status.UNBROADCASTED) {
            broadcaster.get().broadcast(unit, true);
        }
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

    /**
     * Creates a new {@link SyntaxCollection} instance with the given {@link Broadcaster} to broadcast new units.
     *
     * @param <E>
     *            Type of items the collection contains.
     * @param broadcaster
     *            Used to broadcast new units.
     *
     * @return The new {@link SyntaxCollection} instance.
     */
    public static <E extends Syntax & Groupable<E>> SyntaxCollection<E> create(Broadcaster broadcaster) {
        return new StandardSyntaxCollection<>(broadcaster);
    }

    @Override
    public SyntaxCollection<T> broadcaster(Broadcaster broadcaster) {
        this.broadcaster = Optional.fromNullable(broadcaster);
        return this;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        // make a defensive copy as this collection may be modified as a result of broadcasting
        ImmutableList<T> units = ImmutableList.copyOf(list);

        for (T unit : units) {
            unit.propagateBroadcast(broadcaster);
        }
    }
}
