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
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;

/**
 * Something that can be broadcasted.
 *
 * @author nmcwilliams
 * @see Broadcaster
 * @see EmittingBroadcaster
 * @see Syntax
 */
public interface Broadcastable {
    /**
     * Sets the current broadcast status. Normally for internal use. Do not call without understanding the implications.
     *
     * @param status
     *     The new status.
     */
    void status(Status status);

    /**
     * Gets the current broadcast status of this unit.
     * <p>
     * This primarily determines whether this unit should be broadcasted again.
     *
     * @return The current broadcast status.
     */
    Status status();

    /**
     * Broadcasts or rebroadcasts all child units using the given {@link Broadcaster}.
     * <p>
     * The broadcast will only occur for a unit if its {@link Status} matches the given {@link Status}.
     * <p>
     * Implementers should call this on child units and {@link SyntaxCollection}s first, then use the broadcaster to broadcast
     * itself. All of this should be wrapped in a check to ensure the {@link Status} matches.
     *
     * @param broadcaster
     *     Use this {@link Broadcaster} to broadcast all unbroadcasted child units.
     * @param status
     *     Broadcast units that have this status.
     */
    void propagateBroadcast(Broadcaster broadcaster, Status status);

    /**
     * Gets whether an in-progress broadcast should be stopped.
     * <p>
     * This might be true if a change of state or conditions of the unit result in the broadcast no longer being necessary during
     * the given {@link SubscriptionPhase}.
     *
     * @param phase
     *     The current {@link SubscriptionPhase}.
     *
     * @return True if in-progress broadcasting should be stopped.
     */
    boolean breakBroadcast(SubscriptionPhase phase);
}
