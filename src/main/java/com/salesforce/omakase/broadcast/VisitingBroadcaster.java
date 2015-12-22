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

/**
 j,  * ADD LICENSE
 */
package com.salesforce.omakase.broadcast;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Status;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link Broadcaster} that will store all received broadcasted events. Replay the broadcasts using {@link #visit()}. The
 * broadcasts can be replayed multiple times.
 *
 * @author nmcwilliams
 */
public final class VisitingBroadcaster extends AbstractBroadcaster {
    private final List<Broadcastable> list = Lists.newArrayListWithExpectedSize(64);
    private boolean visiting;

    /**
     * Constructs a new {@link VisitingBroadcaster} instance that will relay all broadcasted events to the given {@link
     * Broadcaster}.
     *
     * @param relay
     *     Wrap (decorate) this broadcaster. All broadcasts will be relayed to this one.
     */
    public VisitingBroadcaster(Broadcaster relay) {
        wrap(checkNotNull(relay, "relay cannot be null"));
    }

    @Override
    public void broadcast(Broadcastable broadcastable) {
        list.add(broadcastable);

        // update status to prevent a unit from being broadcasted too many times
        if (broadcastable.status() == Status.UNBROADCASTED) {
            broadcastable.status(Status.QUEUED);
        }

        // while a visit is in progress, immediately send out any received broadcasts (can occur if a refinement results
        // in new syntax instances, or rework results in new syntax units being added).
        if (visiting) {
            relay.broadcast(broadcastable);
        }
    }

    /** Replays all broadcasted events. */
    public void visit() {
        visiting = true;

        // make a defensive copy since the list may be modified as a result of this call
        ImmutableList<Broadcastable> snapshot = ImmutableList.copyOf(list);

        for (Broadcastable broadcastable : snapshot) {
            relay.broadcast(broadcastable);
        }

        visiting = false;
    }
}
