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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.util.As;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class for {@link Syntax} units.
 *
 * @author nmcwilliams
 */
public abstract class AbstractSyntax implements Syntax {
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

    /**
     * For implementations: do not copy comments or orphaned comments, instead be sure to call {@link #copiedFrom(Syntax)} on the
     * new copy.
     */
    @Override
    public abstract Syntax copy(); // overriding just to comment on subclass implementations as done above.

    /**
     * This should be called on all copied units. It handles shared logic such as copying comments.
     * <p/>
     * Examples:
     * <code><pre>
     *     Rule copy = new Rule().copiedFrom(original);
     * </pre></code>
     *
     * @param original
     *     The original, copied unit.
     *
     * @return this, for chaining.
     */
    @SuppressWarnings("unchecked")
    protected final <T extends Syntax> T copiedFrom(T original) {
        this.comments(original).orphanedComments(original);
        return (T)this;
    }

    @Override
    public Syntax comment(String comment) {
        return comment(new Comment(comment));
    }

    @Override
    public Syntax comment(Comment comment) {
        checkNotNull(comment, "comment cannot be null");
        getOrCreateComments(4).add(comment);
        return this;
    }

    @Override
    public Syntax comments(List<String> comments) {
        if (comments == null || comments.isEmpty()) return this;

        getOrCreateComments(comments.size());

        for (String comment : comments) {
            this.comments.add(new Comment(comment));
        }

        return this;
    }

    @Override
    public Syntax comments(Syntax copyFrom) {
        ImmutableList<Comment> toCopy = copyFrom.comments();
        if (toCopy.isEmpty()) return this;

        getOrCreateComments(toCopy.size()).addAll(toCopy);
        return this;
    }

    @Override
    public ImmutableList<Comment> comments() {
        return comments == null ? ImmutableList.<Comment>of() : ImmutableList.copyOf(comments);
    }

    @Override
    public Syntax orphanedComments(List<String> comments) {
        if (comments == null || comments.isEmpty()) return this;

        getOrCreateOrphanedComments(comments.size());

        for (String comment : comments) {
            this.orphanedComments.add(new Comment(comment));
        }

        return this;
    }

    @Override
    public Syntax orphanedComments(Syntax copyFrom) {
        ImmutableList<Comment> toCopy = copyFrom.orphanedComments();
        if (toCopy.isEmpty()) return this;

        getOrCreateOrphanedComments(toCopy.size()).addAll(toCopy);
        return this;
    }

    @Override
    public ImmutableList<Comment> orphanedComments() {
        return orphanedComments == null ? ImmutableList.<Comment>of() : ImmutableList.copyOf(orphanedComments);
    }

    @Override
    public boolean hasAnnotation(String name) {
        if (comments == null) return false;

        for (Comment comment : comments) {
            if (comment.hasAnnotation(name)) return true;
        }

        return false;
    }

    @Override
    public boolean hasAnnotation(CssAnnotation annotation) {
        if (comments == null) return false;

        for (Comment comment : comments) {
            if (comment.hasAnnotation(annotation)) return true;
        }

        return false;
    }

    @Override
    public Optional<CssAnnotation> annotation(String name) {
        if (comments == null) return Optional.absent();

        for (Comment comment : comments) {
            Optional<CssAnnotation> annotation = comment.annotation(name);
            if (annotation.isPresent()) return annotation;
        }

        return Optional.absent();
    }

    @Override
    public List<CssAnnotation> annotations() {
        List<CssAnnotation> found = new ArrayList<>();

        if (comments != null) {
            for (Comment comment : comments) {
                Optional<CssAnnotation> annotation = comment.annotation();
                if (annotation.isPresent()) found.add(annotation.get());
            }
        }

        return found;
    }

    @Override
    public void annotate(CssAnnotation annotation) {
        getOrCreateComments(4).add(annotation.toComment(true));
    }

    @Override
    public void annotateUnlessPresent(CssAnnotation annotation) {
        if (!hasAnnotation(annotation)) annotate(annotation);
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
        if (this.status == Status.UNBROADCASTED) { // only broadcast ourselves once
            broadcaster.broadcast(this);
        }
    }

    @Override
    public boolean writesOwnComments() {
        return false;
    }

    @Override
    public boolean writesOwnOrphanedComments() {
        return false;
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

    /** utility to ensure the comments list is created before using it */
    private List<Comment> getOrCreateComments(int initialSize) {
        if (comments == null) comments = new ArrayList<>(initialSize);
        return comments;
    }

    /** utility to ensure the orphaned comments list is created before using it */
    private List<Comment> getOrCreateOrphanedComments(int initialSize) {
        if (orphanedComments == null) orphanedComments = new ArrayList<>(initialSize);
        return orphanedComments;
    }
}
