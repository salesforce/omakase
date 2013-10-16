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
import com.google.common.collect.Iterables;
import com.salesforce.omakase.As;
import com.salesforce.omakase.broadcast.Broadcaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for {@link Syntax} units.
 *
 * @author nmcwilliams
 */
public abstract class AbstractSyntax implements Syntax {
    private final int line;
    private final int column;

    private List<Comment> comments;
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
    public Syntax comments(Iterable<String> commentsToAdd) {
        if (commentsToAdd == null) return this;

        // delayed creation of comments list
        if (comments == null) {
            comments = new ArrayList<>(2);
        }

        // add the comments
        for (String comment : commentsToAdd) {
            comments.add(new Comment(comment));
        }

        return this;
    }

    @Override
    public Syntax directComments(Iterable<Comment> commentsToAdd) {
        if (commentsToAdd == null) return this;

        // delayed creation of comments list
        if (comments == null) {
            comments = new ArrayList<>(2);
        }

        Iterables.addAll(comments, commentsToAdd);
        return this;
    }

    @Override
    public List<Comment> comments() {
        return comments == null ? ImmutableList.<Comment>of() : ImmutableList.copyOf(comments);
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
    public String toString() {
        return As.stringNamed("")
            .addIf(line > -1, "line", line)
            .addIf(column > -1, "column", column)
            .addUnlessEmpty("comments", comments())
            .toString();
    }
}
