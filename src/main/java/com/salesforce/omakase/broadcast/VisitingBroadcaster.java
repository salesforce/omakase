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

import com.salesforce.omakase.ast.Status;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A broadcaster that can replay the broadcast chain on demand.
 *
 * @author nmcwilliams
 */
public final class VisitingBroadcaster extends AbstractBroadcaster {
    private Broadcastable target;
    private boolean locked;

    /**
     * Creates a new {@link VisitingBroadcaster}.
     */
    public VisitingBroadcaster() {}

    /**
     * Creates a new {@link VisitingBroadcaster} and calls {@link #chain(Broadcaster)} on this instance, passing in the
     * given {@link Broadcaster}.
     *
     * @param broadcaster
     *     Add this broadcaster to the end of the chain.
     */
    public VisitingBroadcaster(Broadcaster broadcaster) {
        chain(broadcaster);
    }

    @Override
    public void broadcast(Broadcastable broadcastable) {
        if (!locked) {
            target = broadcastable;
        }
        relay(broadcastable);
    }

    /**
     * Calls {@link Broadcastable#propagateBroadcast(Broadcaster, Status)} on the top unit using the given {@link Broadcaster}
     * and status.
     * <p>
     * Once this method is called the target unit is locked in so that subsequent calls will always propagate from the same
     * starting point.
     *
     * @param broadcaster
     *     Propagate with this broadcaster.
     * @param status
     *     Only propagate the broadcast of units with this status.
     */
    public void visit(Broadcaster broadcaster, Status status) {
        checkNotNull(broadcaster, "broadcaster cannot be null");
        checkNotNull(status, "status cannot be null");

        locked = true;
        if (target != null) {
            target.propagateBroadcast(broadcaster, status);
        }
    }
}
