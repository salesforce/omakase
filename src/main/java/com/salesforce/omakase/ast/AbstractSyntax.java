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

package com.salesforce.omakase.ast;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.util.As;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for {@link Syntax} units.
 *
 * @param <C>
 *     (C)opiedType. Type of unit created when copied (usually the same type as the implementing class itself).
 *
 * @author nmcwilliams
 */
public abstract class AbstractSyntax<C extends Syntax<C>> implements Syntax<C> {
    private static final AtomicInteger sequence = new AtomicInteger();
    private final int id = sequence.incrementAndGet();

    private final int line;
    private final int column;

    private List<Comment> comments;
    private List<Comment> orphanedComments;
    private Status status = Status.UNBROADCASTED;

    /** Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units). */
    public AbstractSyntax() {
        this(-1, -1);
    }

    /**
     * Creates a new instance with the given line and column numbers.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     */
    public AbstractSyntax(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int column() {
        return column;
    }

    @Override
    public boolean hasSourcePosition() {
        return line != -1 && column != -1;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public final C copy() {
        return copy(null, null);
    }

    @Override
    public final C copy(Prefix prefix, SupportMatrix support) {
        C copy = makeCopy(prefix, support);
        if (comments != null) copy.comments(this);
        if (orphanedComments != null) copy.orphanedComments(this);
        return copy;
    }

    /**
     * Inner method to perform a deep copy of the instance.
     * <p/>
     * For implementations, take note that the prefix and support params may be null. Do <em>not</em> copy comments or orphaned
     * comments as this is already handled. If applicable and present, the prefix and support should be used to vendor prefix
     * certain values. Prefixes should only be added if the {@link SupportMatrix} has a browser version that requires it. See
     * {@link #copy (Prefix, SupportMatrix)} for more details.
     *
     * @param prefix
     *     Apply this {@link Prefix} is applicable.
     * @param support
     *     Represents the supported browser versions.
     *
     * @return The new copy.
     *
     * @see #copy()
     * @see #copy(Prefix, SupportMatrix)
     */
    protected abstract C makeCopy(Prefix prefix, SupportMatrix support);

    @Override
    public Syntax<C> comments(List<String> comments) {
        if (comments == null || comments.isEmpty()) return this;

        // delayed creation of comments list
        if (this.comments == null) {
            this.comments = new ArrayList<Comment>(comments.size());
        }

        // add the comments
        for (String comment : comments) {
            this.comments.add(new Comment(comment));
        }

        return this;
    }

    @Override
    public Syntax<C> comments(Syntax<?> copyFrom) {
        ImmutableList<Comment> toCopy = copyFrom.comments();
        if (toCopy.isEmpty()) return this;

        // delayed creation of comments list
        if (comments == null) {
            comments = new ArrayList<Comment>(toCopy.size());
        }

        comments.addAll(toCopy);
        return this;
    }

    @Override
    public ImmutableList<Comment> comments() {
        return comments == null ? ImmutableList.<Comment>of() : ImmutableList.copyOf(comments);
    }

    @Override
    public Syntax<C> orphanedComments(List<String> comments) {
        if (comments == null || comments.isEmpty()) return this;

        // delayed creation of comments list
        if (this.orphanedComments == null) {
            this.orphanedComments = new ArrayList<Comment>(comments.size());
        }

        // add the comments
        for (String comment : comments) {
            this.orphanedComments.add(new Comment(comment));
        }

        return this;
    }

    @Override
    public Syntax<C> orphanedComments(Syntax<?> copyFrom) {
        ImmutableList<Comment> toCopy = copyFrom.orphanedComments();
        if (toCopy.isEmpty()) return this;

        // delayed creation of comments list
        if (orphanedComments == null) {
            orphanedComments = new ArrayList<Comment>(toCopy.size());
        }

        orphanedComments.addAll(toCopy);
        return this;
    }

    @Override
    public ImmutableList<Comment> orphanedComments() {
        return orphanedComments == null ? ImmutableList.<Comment>of() : ImmutableList.copyOf(orphanedComments);
    }

    @Override
    public void status(Status status) {
        this.status = status;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        // only broadcast ourselves once
        if (this.status == Status.UNBROADCASTED) {
            broadcaster.broadcast(this);
        }
    }

    @Override
    public final int hashCode() {
        // final because the basic broadcasting behavior assumes identity-based equality. In addition,
        // there is no universally logical non-identity-based implementation of hashCode and equals that applies to all of the
        // different usages of AST objects. The definition can vary from one plugin to the next. Thus,
        // when equality must take on a different meaning it must be dealt with at the container level.
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        // final because the basic broadcasting behavior assumes identity-based equality. In addition,
        // there is no universally logical non-identity-based implementation of hashCode and equals that applies to all of the
        // different usages of AST objects. The definition can vary from one plugin to the next. Thus,
        // when equality must take on a different meaning it must be dealt with at the container level.
        return super.equals(obj);
    }

    @Override
    public final String toString() {
        // this doesn't have to be final...it's just final as a reminder that usually it shouldn't be added because this
        // default implementation is good enough.
        return As.string(this).fields().toString();
    }
}
