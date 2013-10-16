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
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Comment;
import com.salesforce.omakase.ast.Syntax;

import java.util.ArrayList;
import java.util.List;

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
public abstract class AbstractGroupable<P, T extends Syntax & Groupable<P, T>> extends AbstractSyntax implements Groupable<P, T> {
    private SyntaxCollection<P, T> group;
    private List<Comment> orphanedComments;

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
        return isDetached() || group.first().get().equals(this);
    }

    @Override
    public boolean isLast() {
        return isDetached() || group.last().get().equals(this);
    }

    @Override
    public Groupable<P, T> prepend(T unit) {
        checkNotNull(unit, "unit cannot be null");
        checkState(!isDetached(), "currently not part of any group!");
        group.prependBefore(self(), unit);
        return this;
    }

    @Override
    public Groupable<P, T> append(T unit) {
        checkNotNull(unit, "unit cannot be null");
        checkState(!isDetached(), "currently not part of any group!");
        group.appendAfter(self(), unit);
        return this;
    }

    @Override
    public void detach() {
        if (isDetached()) return;
        group.detach(self());
        this.group = null;
    }

    @Override
    public boolean isDetached() {
        return group == null;
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
        return isDetached() ? Optional.<P>absent() : Optional.of(group().get().parent());
    }

    @Override
    public boolean isWritable() {
        return !isDetached();
    }

    @Override
    public void orphanedComment(Comment comment) {
        checkNotNull(comment, "comment cannot be null");
        orphanedComments = (orphanedComments == null) ? new ArrayList<Comment>() : orphanedComments;
        orphanedComments.add(comment);
    }

    @Override
    public List<Comment> orphanedComments() {
        return orphanedComments == null ? ImmutableList.<Comment>of() : ImmutableList.copyOf(orphanedComments);
    }


}
