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

package com.salesforce.omakase.ast.notification;

import com.salesforce.omakase.ast.Comment;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.List;

/**
 * Base class for notification-only objects.
 *
 * @author nmcwilliams
 */
class AbstractNotification implements Syntax {
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
        return true;
    }

    @Override
    public void comments(Iterable<String> commentsToAdd) {
        throw new UnsupportedOperationException("unsupported operation on a notification-only object.");
    }

    @Override
    public List<Comment> comments() {
        throw new UnsupportedOperationException("unsupported operation on a notification-only object.");
    }

    @Override
    public Syntax status(Status status) {
        return this;
    }

    @Override
    public Status status() {
        return Status.UNBROADCASTED;
    }

    @Override
    public Syntax broadcaster(Broadcaster broadcaster) {
        return this;
    }

    @Override
    public Broadcaster broadcaster() {
        throw new UnsupportedOperationException("unsupported operation on a notification-only object.");
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        throw new UnsupportedOperationException("unsupported operation on a notification-only object.");
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        throw new UnsupportedOperationException("unsupported operation on a notification-only object.");
    }
}
