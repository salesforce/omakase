/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.ast.collection;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.util.As;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.*;

/**
 * Standard (default) implementation of the {@link SyntaxCollection}.
 * <p>
 * This uses a linked-node approach optimized for random lookups, insertions and removals. Uniqueness is maintained like a set and
 * prevents duplicates. Appending or prepending an existing unit will simply move it's position.
 *
 * @param <P>
 *     Type of the (P)arent object containing this collection (e.g., {@link SelectorPart}s have {@link Selector}s as the parent).
 * @param <T>
 *     The (T)ype of units to be grouped with.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("AutoBoxing")
public final class LinkedSyntaxCollection<P, T extends Groupable<P, T>> implements SyntaxCollection<P, T> {
    private final P parent;
    private final Lookup<T> lookup = new Lookup<>();

    private Node<T> first;
    private Node<T> last;
    private Broadcaster broadcaster;

    /**
     * Creates a new {@link LinkedSyntaxCollection} with no available {@link Broadcaster}.
     *
     * @param parent
     *     The parent that owns this collection. Do not pass null.
     */
    public LinkedSyntaxCollection(P parent) {
        this(parent, null);
    }

    /**
     * Creates a new {@link LinkedSyntaxCollection} using the given {@link Broadcaster} to broadcast new units.
     *
     * @param parent
     *     The parent that owns this collection. Do not pass null.
     * @param broadcaster
     *     Used to broadcast new units.
     */
    public LinkedSyntaxCollection(P parent, Broadcaster broadcaster) {
        this.parent = parent;
        this.broadcaster = broadcaster;
    }

    @Override
    public int size() {
        return lookup.size();
    }

    @Override
    public boolean isEmpty() {
        return first == null;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current;

            @Override
            public boolean hasNext() {
                return current == null ? first != null : current.next != null;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                current = (current == null) ? first : current.next;
                return current.unit;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException(); // can be implemented when needed
            }
        };
    }

    @Override
    public boolean isEmptyOrNoneWritable() {
        if (isEmpty()) return true;
        for (T unit : this) {
            if (unit.isWritable()) return false;
        }
        return true;
    }

    @Override
    public boolean contains(T unit) {
        return lookup.contains(unit.id());
    }

    @Override
    public Optional<T> first() {
        return first == null ? Optional.<T>absent() : Optional.of(first.unit);
    }

    @Override
    public Optional<T> last() {
        return last == null ? Optional.<T>absent() : Optional.of(last.unit);
    }

    @Override
    public Optional<T> next(T unit) {
        Node<T> node = lookup.get(unit.id());
        if (node == null) throw new IllegalArgumentException("the specified unit does not exist in this collection!");
        if (node.next == null) return Optional.absent();
        return Optional.of(node.next.unit);
    }

    @Override
    public Optional<T> previous(T unit) {
        Node<T> node = lookup.get(unit.id());
        if (node == null) throw new IllegalArgumentException("the specified unit does not exist in this collection!");
        if (node.previous == null) return Optional.absent();
        return Optional.of(node.previous.unit);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S extends T> Optional<S> find(Class<S> klass) {
        // cast is safe because we ensure S extends T, and the predicate only returns true for types of S.
        return (Optional<S>)Iterators.tryFind(iterator(), Predicates.instanceOf(klass));
    }

    @Override
    public SyntaxCollection<P, T> prepend(T unit) {
        checkNotNull(unit, "unit cannot be null");
        checkArgument(!unit.isDestroyed(), "cannot prepend a destroyed unit!");

        // create a new node
        first = new Node<>(null, first, unit);
        lookup.put(unit.id(), first);
        if (last == null) last = first;

        // perform associative actions on the unit
        associate(unit);

        return this;
    }

    @Override
    public SyntaxCollection<P, T> prependAll(Iterable<T> units) {
        for (T unit : ImmutableList.copyOf(units).reverse()) prepend(unit);
        return this;
    }

    @Override
    public SyntaxCollection<P, T> prependBefore(T index, T unit) throws IllegalArgumentException {
        checkNotNull(index, "exiting cannot be null");
        checkNotNull(unit, "unit cannot be null");
        checkArgument(!unit.isDestroyed(), "cannot prepend a destroyed unit!");

        // find the node for the index unit
        Node<T> node = lookup.get(index.id());
        if (node == null) throw new IllegalArgumentException("the specified unit does not exist in this collection!");

        // if the index unit is the first unit then delegate to #prepend
        if (node == first || isEmpty()) return prepend(unit);

        // create a new node
        lookup.put(unit.id(), new Node<>(node.previous, node, unit));

        // perform associative actions on the unit
        associate(unit);

        return this;
    }

    @Override
    public SyntaxCollection<P, T> append(T unit) {
        checkNotNull(unit, "unit cannot be null");
        checkArgument(!unit.isDestroyed(), "cannot append a destroyed unit!");

        // create a new node
        last = new Node<>(last, null, unit);
        lookup.put(unit.id(), last);
        if (first == null) first = last;

        // perform associative actions on the unit
        associate(unit);

        return this;
    }

    @Override
    public SyntaxCollection<P, T> appendAll(Iterable<T> units) {
        for (T unit : units) append(unit);
        return this;
    }

    @Override
    public SyntaxCollection<P, T> appendAfter(T index, T unit) throws IllegalArgumentException {
        checkNotNull(index, "exiting cannot be null");
        checkNotNull(unit, "unit cannot be null");
        checkArgument(!unit.isDestroyed(), "cannot append a destroyed unit!");

        // find the node for the index unit
        Node<T> node = lookup.get(index.id());
        if (node == null) throw new IllegalArgumentException("the specified unit does not exist in this collection!");

        // if the index unit is the last unit then delegate to #append
        if (node == last || (node.previous == null && node.next == null)) return append(unit);

        // create a new node
        lookup.put(unit.id(), new Node<>(node, node.next, unit));

        // perform associative actions on the unit
        associate(unit);

        return this;
    }

    @Override
    public SyntaxCollection<P, T> remove(T unit) {
        Node<T> removed = lookup.remove(unit.id());

        if (removed != null) {
            // update our links
            unlink(removed);

            // ensure the unit is not associated with this group any longer
            unit.group(null);
        }

        return this;
    }

    @Override
    public SyntaxCollection<P, T> clear() {
        for (T unit : this) remove(unit);
        return this;
    }

    @Override
    public SyntaxCollection<P, T> replaceExistingWith(T unit) {
        return clear().append(unit);
    }

    @Override
    public SyntaxCollection<P, T> replaceExistingWith(Iterable<T> units) {
        return clear().appendAll(units);
    }

    @Override
    public void destroyAll() {
        for (T unit : this) {
            unit.destroy();
        }
    }

    @Override
    public P parent() {
        return parent;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        this.broadcaster = broadcaster;  // save a reference so that subsequent appended/prepended units will be broadcasted
        for (T unit : this) unit.propagateBroadcast(broadcaster);
    }

    @Override
    public String toString() {
        return As.string(this).add("units", Lists.newArrayList(iterator())).toString();
    }

    private void associate(T unit) {
        unit.unlink().group(this);

        // broadcast the unit if it hasn't been broadcasted yet.
        if (broadcaster != null && unit.status() == Status.UNBROADCASTED) unit.propagateBroadcast(broadcaster);
    }

    private void unlink(Node<T> node) {
        if (node == first) first = node.next;
        if (node == last) last = node.previous;
        if (node.previous != null) node.previous.next = node.next;
        if (node.next != null) node.next.previous = node.previous;
    }

    private static final class Node<E> {
        Node<E> previous;
        Node<E> next;
        final E unit;

        Node(Node<E> previous, Node<E> next, E unit) {
            this.unit = unit;
            this.next = next;
            this.previous = previous;

            if (previous != null) previous.next = this;
            if (next != null) next.previous = this;
        }
    }

    private static final class Lookup<E extends Syntax> {
        private List<Node<E>> sparse = new ArrayList<>();
        private Map<Integer, Node<E>> dense;
        private int count = 0;

        public void put(int id, Node<E> node) {
            // for small collections use an array list, for larger use a map
            if (count < 64) {
                sparse.add(node);
            } else if (count == 64) {
                dense = new HashMap<>(128);
                for (Node<E> n : sparse) {
                    dense.put(n.unit.id(), n);
                }
                dense.put(id, node);
                sparse = null;
            } else {
                dense.put(id, node);
            }
            count++;
        }

        public Node<E> get(int id) {
            if (sparse != null) {
                for (Node<E> n : sparse) {
                    if (n.unit.id() == id) return n;
                }
                return null;
            }
            return dense.get(id);
        }

        public Node<E> remove(int id) {
            if (sparse != null) {
                for (Iterator<Node<E>> it = sparse.iterator(); it.hasNext(); ) {
                    Node<E> next = it.next();
                    if (next.unit.id() == id) {
                        it.remove();
                        return next;
                    }
                }
                return null;
            }
            return dense.remove(id);
        }

        public int size() {
            return sparse != null ? sparse.size() : dense.size();
        }

        public boolean isEmpty() {
            return sparse != null ? sparse.isEmpty() : dense.isEmpty();
        }

        public boolean contains(int id) {
            if (sparse != null) {
                for (Node<E> n : sparse) {
                    if (n.unit.id() == id) return true;
                }
                return false;
            }
            return dense.containsKey(id);
        }
    }
}
