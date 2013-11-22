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
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.util.As;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Standard (default) implementation of the {@link SyntaxCollection}.
 *
 * @param <P>
 *     Type of the (P)arent object containing this collection (e.g., {@link SelectorPart}s have {@link Selector}s as the parent).
 * @param <T>
 *     The (T)ype of units to be grouped with.
 *
 * @author nmcwilliams
 */
public final class StandardSyntaxCollection<P, T extends Groupable<P, T>> implements SyntaxCollection<P, T> {
    private final transient P parent;
    private final LinkedList<T> list;
    private transient Broadcaster broadcaster;

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
        this.list = new LinkedList<T>();
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
    @SuppressWarnings("unchecked")
    public <S extends T> Optional<S> find(Class<S> klass) {
        // cast is safe because we ensure S extends T, and the predicate only returns true for types of S.
        return (Optional<S>)Iterables.tryFind(list, Predicates.instanceOf(klass));
    }

    @Override
    public Optional<T> next(T unit) {
        int index = list.indexOf(unit);
        if (index == -1) throw new IllegalArgumentException("the specified unit does not exist in this collection!");
        if (index >= list.size() - 1) return Optional.absent();
        return Optional.of(list.get(index + 1));
    }

    @Override
    public Optional<T> previous(T unit) {
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
    public SyntaxCollection<P, T> prependBefore(T index, T unit) throws IllegalArgumentException {
        checkNotNull(index, "exiting cannot be null");
        checkNotNull(unit, "unit cannot be null");

        int position = list.indexOf(index);
        if (position == -1) throw new IllegalArgumentException("the specified unit does not exist in this collection!");

        // add the unit to the list
        list.add(position, unit);
        unit.group(this);

        // ensure the unit is broadcasted
        broadcast(unit);

        return this;
    }

    @Override
    public SyntaxCollection<P, T> moveBefore(T index, T unit) throws IllegalArgumentException {
        if (!unit.isDetached()) unit.detach();
        return prependBefore(index, unit);
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
    public SyntaxCollection<P, T> moveAfter(T index, T unit) throws IllegalArgumentException {
        if (!unit.isDetached()) unit.detach();
        return appendAfter(index, unit);
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
        return As.string(this).fields().toString();
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
}
