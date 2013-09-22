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

package com.salesforce.omakase.broadcaster;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.salesforce.omakase.ast.Syntax;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TESTME
 * <p/>
 * A broadcaster that queues broadcasts.
 * <p/>
 * Broadcasts can be paused with {@link #pause()} and resumed with {@link #resume()}. The queue operates on a FIFO basis.
 *
 * @author nmcwilliams
 */
public final class QueuingBroadcaster extends AbstractBroadcaster {
    private final Deque<Syntax> queue = new ArrayDeque<>();
    private Set<Syntax> rejected;

    private State state = State.READY;

    private enum State {
        READY,
        PAUSED,
        BROADCASTING
    }

    /**
     * Constructs a new {@link QueuingBroadcaster} instance that will relay all events to the given {@link Broadcaster}.
     *
     * @param relay
     *     Wrap (decorate) this broadcaster. All broadcasts will be relayed to this one.
     */
    public QueuingBroadcaster(Broadcaster relay) {
        wrap(checkNotNull(relay, "relay cannot be null"));
    }

    @Override
    public <T extends Syntax> void broadcast(T syntax) {
        queue.addLast(syntax);

        if (state == State.PAUSED || state == State.BROADCASTING) return;

        flush();
    }

    /** Pauses the queue. While pause, all broadcasts will be stored and will not be relayed until {@link #resume()} is called. */
    public void pause() {
        state = State.PAUSED;
    }

    /** Resumes broadcasts. Any broadcasts currently in the queue will be immediately sent out. */
    public void resume() {
        flush();
        state = State.READY;
    }

    /**
     * Gets the count of the items currently in the queue.
     *
     * @return Number of items in the queue.
     */
    public int size() {
        return queue.size();
    }

    /**
     * Retrieves, but does not remove the first item in the queue.
     *
     * @return The first item in the queue.
     */
    public Syntax peek() {
        return queue.peekFirst();
    }

    /**
     * Retrieves, but does not remove the last item in the queue.
     *
     * @return The last item in the queue.
     */
    public Syntax peekLast() {
        return queue.peekLast();
    }

    /**
     * Gets a <em>copy</em> of all units in the queue. Avoid this method if possible.
     *
     * @return The copy of all units in the queue.
     */
    public Iterable<Syntax> all() {
        return ImmutableList.copyOf(queue);
    }

    /**
     * Prevents the given {@link Syntax} unit, if it currently exists in the queue, from being broadcasted once {@link #resume()}
     * is called.
     *
     * @param syntax
     *     The {@link Syntax} unit to reject.
     *
     * @return this, for chaining.
     */
    public QueuingBroadcaster reject(Syntax syntax) {
        if (rejected == null) {
            rejected = Sets.newHashSet();
        }
        rejected.add(syntax);
        return this;
    }

    /** Broadcasts all events currently in the queue, until the queue is empty. */
    private void flush() {
        while (!queue.isEmpty()) {
            // grab the next item in the queue.
            Syntax queued = queue.removeFirst();

            // broadcast the unit unless it has been rejected
            if (rejected == null || !rejected.contains(queued)) {
                relay.broadcast(queued);
            }
        }

        // clear out the rejections
        rejected = null;
    }
}
