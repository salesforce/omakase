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

import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;

import java.util.Optional;

import static com.google.common.base.Preconditions.*;

/**
 * Base class for {@link Groupable}s.
 *
 * @param <T>
 *     See "T" described in the {@link Groupable} documentation.
 * @param <P>
 *     See "P" described in the {@link Groupable} documentation.
 *
 * @author nmcwilliams
 */
public abstract class AbstractGroupable<P, T extends Groupable<P, T>> extends AbstractSyntax implements Groupable<P, T> {
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
        return group == null ? Optional.empty() : group.previous(self());
    }

    @Override
    public Optional<T> next() {
        return group == null ? Optional.empty() : group.next(self());
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
    public Groupable<P, T> replaceWith(T unit) {
        checkNotNull(unit, "unit cannot be null");
        checkState(!destroyed, "cannot operate on a destroyed unit!");
        checkState(group != null, "cannot append to an isolated unit");
        prepend(unit);
        destroy();
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
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public Groupable<P, T> group(SyntaxCollection<P, T> group) {
        this.group = group;
        return this;
    }

    @Override
    public SyntaxCollection<P, T> group() {
        return group;
    }

    @Override
    public P parent() {
        return group == null ? null : group().parent();
    }

    @Override
    public boolean isWritable() {
        return !destroyed;
    }
}
