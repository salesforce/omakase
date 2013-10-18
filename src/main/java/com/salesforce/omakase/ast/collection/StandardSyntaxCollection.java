/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.ast.collection;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.Broadcaster;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Standard (default) implementation of the {@link SyntaxCollection}.
 *
 * @param <T>
 *     Type of items in the {@link SyntaxCollection}.
 * @param <P>
 *     Type of the parent object containing this collection (e.g., {@link SelectorPart}s have {@link Selector}s as the parent).
 *
 * @author nmcwilliams
 */
public final class StandardSyntaxCollection<P, T extends Syntax & Groupable<P, T>> implements SyntaxCollection<P, T> {
    private final P parent;
    private final LinkedList<T> list;
    private Broadcaster broadcaster;

    /**
     * Creates a new {@link StandardSyntaxCollection} with no available {@link Broadcaster}.
     *
     * @param parent
     *     The parent that owns this collection. Do not pass null.
     */
    public StandardSyntaxCollection(P parent) {
        this(parent, null);
    }

    /**
     * Creates a new {@link StandardSyntaxCollection} using the given {@link Broadcaster} to broadcast new units.
     *
     * @param parent
     *     The parent that owns this collection. Do not pass null.
     * @param broadcaster
     *     Used to broadcast new units.
     */
    public StandardSyntaxCollection(P parent, Broadcaster broadcaster) {
        this.parent = parent;
        this.list = new LinkedList<>();
        this.broadcaster = broadcaster;
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
    public boolean isEmptyOrNoneWritable() {
        for (T unit : list) {
            if (unit.isWritable()) return false;
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
    public Optional<T> next(T unit) {
        // TESTME
        int index = list.indexOf(unit);
        if (index == -1) throw new IllegalArgumentException("the specified unit does not exist in this collection!");
        if (index >= list.size()) return Optional.absent();
        return Optional.of(list.get(index + 1));
    }

    @Override
    public Optional<T> previous(T unit) {
        // TESTME
        int index = list.indexOf(unit);
        if (index == -1) throw new IllegalArgumentException("the specified unit does not exist in this collection!");
        if (index == 0) return Optional.absent();
        return Optional.of(list.get(index - 1));
    }

    @Override
    public SyntaxCollection<P, T> prepend(T unit) {
        checkNotNull(unit, "unit cannot be null");

        // add the unit to the list
        list.push(unit);
        unit.group(this);

        // ensure the unit has been broadcasted
        broadcast(unit);

        return this;
    }

    @Override
    public SyntaxCollection<P, T> prependAll(Iterable<T> units) {
        for (T unit : ImmutableList.copyOf(units).reverse()) {
            prepend(unit);
        }
        return this;
    }

    @Override
    public SyntaxCollection<P, T> prependBefore(T existing, T unit) throws IllegalArgumentException {
        checkNotNull(existing, "exiting cannot be null");
        checkNotNull(unit, "unit cannot be null");

        int index = list.indexOf(existing);
        if (index == -1) throw new IllegalArgumentException("the specified unit does not exist in this collection!");

        // add the unit to the list
        list.add(index, unit);
        unit.group(this);

        // ensure the unit is broadcasted
        broadcast(unit);

        return this;
    }

    @Override
    public SyntaxCollection<P, T> append(T unit) {
        checkNotNull(unit, "unit cannot be null");

        // add the unit to the list
        list.add(unit);
        unit.group(this);

        // ensure the unit is broadcasted
        broadcast(unit);

        return this;
    }

    @Override
    public SyntaxCollection<P, T> appendAll(Iterable<T> units) {
        for (T unit : units) {
            append(unit);
        }
        return this;
    }

    @Override
    public SyntaxCollection<P, T> appendAfter(T existing, T unit) throws IllegalArgumentException {
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
        unit.group(this);

        // ensure the unit is broadcasted
        broadcast(unit);

        return this;
    }

    @Override
    public SyntaxCollection<P, T> replaceExistingWith(Iterable<T> units) {
        clear();
        appendAll(units);
        return this;
    }

    @Override
    public SyntaxCollection<P, T> detach(T unit) {
        boolean removed = list.remove(unit);

        // the unit must have existed in this collection
        if (!removed) throw new IllegalArgumentException("the specified unit does not exist in this collection!");

        // unset the group (and transitively the parent)
        unit.group(null);

        return this;
    }

    @Override
    public Iterable<T> clear() {
        List<T> detached = ImmutableList.copyOf(list);

        for (T unit : detached) {
            detach(unit);
        }

        return detached;
    }

    @Override
    public P parent() {
        return parent;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        // save a reference so that subsequent appended/prepended units will be broadcasted
        this.broadcaster = broadcaster;

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
        if (broadcaster != null && unit.status() == Status.UNBROADCASTED) {
            broadcaster.broadcast(unit, true);
        }
    }

    /**
     * Creates a new {@link SyntaxCollection} instance.
     * <p/>
     * Example:
     * <p/>
     * {@code StandardSyntaxCollection.create(theParentInstance)}
     *
     * @param <E>
     *     Type of items the collection contains.
     * @param parent
     *     The parent that owns this collection.
     *
     * @return The new {@link SyntaxCollection} instance.
     */
    public static <P, E extends Syntax & Groupable<P, E>> SyntaxCollection<P, E> create(P parent) {
        return new StandardSyntaxCollection<>(parent);
    }

    /**
     * Creates a new {@link SyntaxCollection} instance with the given {@link Broadcaster} to broadcast new units.
     * <p/>
     * Example:
     * <p/>
     * {@code StandardSyntaxCollection.create(theParentInstance, theBroadcasterInstance)}
     *
     * @param <E>
     *     Type of items the collection contains.
     * @param parent
     *     The parent that owns this collection.
     * @param broadcaster
     *     Used to broadcast new units.
     *
     * @return The new {@link SyntaxCollection} instance.
     */
    public static <P, E extends Syntax & Groupable<P, E>> SyntaxCollection<P, E> create(P parent, Broadcaster broadcaster) {
        return new StandardSyntaxCollection<>(parent, broadcaster);
    }
}
