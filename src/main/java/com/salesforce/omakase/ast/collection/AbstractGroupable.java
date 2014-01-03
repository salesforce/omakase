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
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;

import static com.google.common.base.Preconditions.*;

/**
 * Base class for {@link Groupable}s.
 *
 * @param <T>
 *     Same type as the {@link Groupable}.
 * @param <P>
 *     Same type as for the {@link Groupable}.
 *
 * @author nmcwilliams
 */
public abstract class AbstractGroupable<P, T extends Groupable<P, T>> extends AbstractSyntax<T> implements Groupable<P, T> {
    private SyntaxCollection<P, T> group;
    private boolean destroyed;

    /** Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units). */
    public AbstractGroupable() {}

    /**
     * Creates a new instance with the given line and column numbers.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     */
    public AbstractGroupable(int line, int column) {
        super(line, column);
    }

    /**
     * Should return "this". This is needed for property type access in the {@link AbstractGroupable} class.
     *
     * @return "this".
     */
    protected abstract T self();

    @Override
    public boolean isFirst() {
        return group == null || group.first().get().equals(this);
    }

    @Override
    public boolean isLast() {
        return group == null || group.last().get().equals(this);
    }

    @Override
    public Optional<T> previous() {
        return group == null ? Optional.<T>absent() : group.previous(self());
    }

    @Override
    public Optional<T> next() {
        return group == null ? Optional.<T>absent() : group.next(self());
    }

    @Override
    public Groupable<P, T> prepend(T unit) {
        checkNotNull(unit, "unit cannot be null");
        checkState(!destroyed, "cannot operate on a destroyed unit!");
        checkState(group != null, "cannot prepend to an isolated unit");
        group.prependBefore(self(), unit);
        return this;
    }

    @Override
    public Groupable<P, T> append(T unit) {
        checkNotNull(unit, "unit cannot be null");
        checkState(!destroyed, "cannot operate on a destroyed unit!");
        checkState(group != null, "cannot append to an isolated unit");
        group.appendAfter(self(), unit);
        return this;
    }

    @Override
    public Groupable<P, T> unlink() {
        if (group != null) group.remove(self());
        group = null;
        return this;
    }

    @Override
    public void destroy() {
        unlink();
        this.destroyed = true;
        status(Status.NEVER_EMIT); // stop further broadcasting
    }

    @Override
    public boolean destroyed() {
        return destroyed;
    }

    @Override
    public Groupable<P, T> group(SyntaxCollection<P, T> group) {
        this.group = group;
        return this;
    }

    @Override
    public Optional<SyntaxCollection<P, T>> group() {
        return Optional.fromNullable(group);
    }

    @Override
    public Optional<P> parent() {
        return group == null ? Optional.<P>absent() : Optional.of(group().get().parent());
    }

    @Override
    public boolean isWritable() {
        return !destroyed;
    }
}
