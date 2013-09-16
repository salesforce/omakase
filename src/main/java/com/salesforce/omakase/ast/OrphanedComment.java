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
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.emitter.SubscribableRequirement;
import com.salesforce.omakase.plugin.Plugin;

import java.util.List;

/**
 * A {@link Comment} which is not directly associated with a subsequent {@link Syntax} unit.
 * <p/>
 * This is for comments that appear at the end of a selector list (before the declaration block), at the end of a declaration
 * block, or at the end of a stylesheet. While most comments are directly linked with the {@link Syntax} unit that follows it,
 * these orphaned comments cannot be logically linked to another subsequent unit because of where they occur.
 * <p/>
 * These are the only comments that are broadcasted. Directives, annotations, or other such metadata may be placed inside of them
 * to use for dynamic processing in a custom {@link Plugin}.
 */
@Subscribable
@Description(value = "A comment unassociated with any syntax unit", broadcasted = SubscribableRequirement.SPECIAL)
public class OrphanedComment extends Comment implements Syntax {
    private Status status = Status.UNBROADCASTED;
    private final Location location;

    /** The type of location where the comment was found. */
    public enum Location {
        /** found at the end of a selector */
        SELECTOR,
        /** found at the end of a declaration */
        DECLARATION,
        /** found at the end of a rule */
        RULE,
        /** found at the end of the stylesheet */
        STYLESHEET
    }

    /**
     * Creates a new {@link OrphanedComment} with the given content.
     *
     * @param content
     *     The comment content.
     * @param location
     *     The type of location where the comment was found.
     */
    public OrphanedComment(String content, OrphanedComment.Location location) {
        super(content);
        this.location = location;
    }

    /**
     * Gets the type of location where this comment was found.
     *
     * @return The type of location where this comment was found, or {@link Optional#absent()} if not specified.
     */
    public OrphanedComment.Location location() {
        return location;
    }

    @Override
    public int line() {
        return -1;
    }

    @Override
    public int column() {
        return -1;
    }

    @Override
    public boolean hasSourcePosition() {
        return false;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public Syntax status(Status status) {
        this.status = status;
        return this;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        // only broadcast ourselves once
        if (this.status == Status.UNBROADCASTED) {
            broadcaster.broadcast(this);
        }
    }

    @Override
    public void comments(Iterable<String> commentsToAdd) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Comment> comments() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Syntax broadcaster(Broadcaster broadcaster) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Broadcaster broadcaster() {
        throw new UnsupportedOperationException();
    }
}
