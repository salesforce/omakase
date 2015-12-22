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

package com.salesforce.omakase.broadcast;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.salesforce.omakase.ast.Status;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A broadcaster that queues broadcasts.
 * <p/>
 * Broadcasts can be paused with {@link #pause()} and resumed with {@link #resume()}. The queue operates on a FIFO basis.
 *
 * @author nmcwilliams
 */
public final class QueuingBroadcaster extends AbstractBroadcaster {
    private final Deque<Broadcastable> queue = new ArrayDeque<>();
    private Set<Class<? extends Broadcastable>> alwaysFlush;
    private Set<Broadcastable> rejected;

    private State state = State.READY;

    private enum State {
        READY,
        PAUSED
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
    public void broadcast(Broadcastable broadcastable) {
        // update status to prevent a unit from being broadcasted too many times
        if (broadcastable.status() == Status.UNBROADCASTED) {
            broadcastable.status(Status.QUEUED);
        }

        // broadcast the unit unless the queue is paused
        if (state == State.READY || (alwaysFlush != null && alwaysFlush.contains(broadcastable.getClass()))) {
            relay.broadcast(broadcastable);
        } else {
            queue.addLast(broadcastable);
        }
    }

    /**
     * Pauses the queue. While pause, all broadcasts will be stored and will not be relayed until {@link #resume()} is called.
     *
     * @return this, for chaining.
     */
    public QueuingBroadcaster pause() {
        state = State.PAUSED;
        return this;
    }

    /**
     * Resumes broadcasts. Any broadcasts currently in the queue will be immediately sent out.
     *
     * @return this, for chaining.
     */
    public QueuingBroadcaster resume() {
        state = State.READY;
        flush();
        return this;
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
    public Broadcastable peek() {
        return queue.peekFirst();
    }

    /**
     * Retrieves, but does not remove the last item in the queue.
     *
     * @return The last item in the queue.
     */
    public Broadcastable peekLast() {
        return queue.peekLast();
    }

    /**
     * Gets a <em>copy</em> of all units in the queue. Avoid this method if possible.
     *
     * @return The copy of all units in the queue.
     */
    public Iterable<Broadcastable> all() {
        return ImmutableList.copyOf(queue);
    }

    /**
     * Prevents the given {@link Broadcastable} unit, if it currently exists in the queue, from being broadcasted once {@link
     * #resume()} is called.
     *
     * @param broadcastable
     *     The {@link Broadcastable} unit to reject.
     *
     * @return this, for chaining.
     */
    public QueuingBroadcaster reject(Broadcastable broadcastable) {
        if (rejected == null) {
            rejected = Sets.newHashSetWithExpectedSize(3);
        }
        rejected.add(broadcastable);
        return this;
    }

    /**
     * Specifies that the broadcasts matching the given class should always be flushed, regardless of whether the queue is paused
     * or not.
     * <p/>
     * Note that the class must be equal-- not an instanceof.
     *
     * @param broadcastable
     *     Always flush {@link Broadcastable}s of this class.
     *
     * @return this, for chaining.
     */
    public QueuingBroadcaster alwaysFlush(Class<? extends Broadcastable> broadcastable) {
        if (alwaysFlush == null) {
            alwaysFlush = Sets.newHashSetWithExpectedSize(3);
        }
        alwaysFlush.add(broadcastable);
        return this;
    }

    /** Broadcasts all events currently in the queue, until the queue is empty. */
    private void flush() {
        while (!queue.isEmpty()) {
            // grab the next item in the queue.
            Broadcastable queued = queue.removeFirst();

            // broadcast the unit unless it has been rejected
            if (rejected == null || !rejected.contains(queued)) {
                relay.broadcast(queued);
            }
        }

        // clear out the rejections
        rejected = null;
    }
}
