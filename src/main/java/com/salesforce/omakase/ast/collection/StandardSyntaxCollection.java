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
 * TESTME
 * <p/>
 * Standard (default) implementation of the {@link SyntaxCollection}.
 *
 * @param <T>
 *     Type of items in the {@link SyntaxCollection}.
 *
 * @author nmcwilliams
 */
public final class StandardSyntaxCollection<T extends Syntax & Groupable<T>> implements SyntaxCollection<T> {
    private final LinkedList<T> list = Lists.newLinkedList();
    private Optional<Broadcaster> broadcaster;

    /** Creates a new {@link StandardSyntaxCollection} with no available {@link Broadcaster}. */
    public StandardSyntaxCollection() {
        this(null);
    }

    /**
     * Creates a new {@link StandardSyntaxCollection} using the given {@link Broadcaster} to broadcast new units.
     *
     * @param broadcaster
     *     Used to broadcast new units.
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

        // if the unit doesn't have a broadcaster and we have one then give it.
        if (unit.broadcaster() == null && broadcaster.isPresent()) {
            unit.broadcaster(broadcaster.get());
        }

        // add the unit to the list
        list.push(unit);
        unit.parent(this);

        // ensure the unit has been broadcasted
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

        // if the unit doesn't have a broadcaster and we have one then give it.
        if (broadcaster.isPresent() && unit.broadcaster() == null) {
            unit.broadcaster(broadcaster.get());
        }

        // add the unit to the list
        list.add(index, unit);
        unit.parent(this);

        // ensure the unit is broadcasted
        broadcast(unit);

        return this;
    }

    @Override
    public SyntaxCollection<T> append(T unit) {
        checkNotNull(unit, "unit cannot be null");

        // if the unit doesn't have a broadcaster and we have one then give it.
        if (broadcaster.isPresent() && unit.broadcaster() == null) {
            unit.broadcaster(broadcaster.get());
        }

        // add the unit to the list
        list.add(unit);
        unit.parent(this);

        // ensure the unit is broadcasted
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

        // add the unit to the list
        if (index == (list.size() - 1)) {
            list.add(unit);
        } else {
            list.add(index + 1, unit);
        }

        // ensure the unit is broadcasted
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
     * Internal method to broadcast newly added units.
     *
     * @param unit
     *     The unit to broadcast.
     */
    private void broadcast(T unit) {
        // only broadcast the unit if it hasn't been broadcasted yet. This also means propagation only happens for unbroadcasted
        // units. Once a unit has been broadcasted it should take care of ensuring any new broadcastable members are properly
        // broadcasted when added. Of course, we can't do anything if we don't have a broadcaster yet,
        // which will be true for dynamically created units with collections, such as rules. In that case,
        // it's vital that propagateBroadcast is called on this collection by the Rule as soon as it gets broadcasted itself.
        if (broadcaster.isPresent() && unit.status() == Status.UNBROADCASTED) {
            broadcaster.get().broadcast(unit, true);
        }
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

    @Override
    public String toString() {
        return As.string(this).indent().add("items", list).toString();
    }

    /**
     * Creates a new {@link SyntaxCollection} instance.
     *
     * @param <E>
     *     Type of items the collection contains.
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
     *     Type of items the collection contains.
     * @param broadcaster
     *     Used to broadcast new units.
     *
     * @return The new {@link SyntaxCollection} instance.
     */
    public static <E extends Syntax & Groupable<E>> SyntaxCollection<E> create(Broadcaster broadcaster) {
        return new StandardSyntaxCollection<>(broadcaster);
    }
}
