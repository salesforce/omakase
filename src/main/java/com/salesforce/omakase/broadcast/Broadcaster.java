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

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.emitter.Emitter;

/**
 * Responsible for broadcasting {@link Broadcastable} events (objects), usually newly created {@link Syntax} units.
 * <p>
 * Implementations should follow the decorator pattern, allowing for nesting of different broadcasters (like Reader).
 *
 * @author nmcwilliams
 * @see Emitter
 */
public interface Broadcaster {
    /**
     * Broadcasts the given event (object).
     *
     * @param broadcastable
     *     The {@link Broadcastable} unit instance that was created.
     */
    void broadcast(Broadcastable broadcastable);

    /**
     * Broadcasts the given event (object).
     * <p>
     * This also gives the option to <em>propagate</em> the broadcast. Propagation directs the broadcasted unit to also broadcast
     * any of it's child or inner unit members. This should usually be specified as true when broadcasting a dynamically created
     * unit (as opposed to one created internally as a result of parsing the source).
     *
     * @param broadcastable
     *     The {@link Broadcastable} unit instance that was created.
     * @param propagate
     *     If {@link Broadcastable#propagateBroadcast(Broadcaster)} should be called on the unit.
     *
     * @see Broadcastable#propagateBroadcast(Broadcaster)
     */
    void broadcast(Broadcastable broadcastable, boolean propagate);

    /**
     * Specifies an inner {@link Broadcaster} to wrap around. This {@link Broadcaster} will receive broadcasted events after this
     * one has processed the event.
     *
     * @param relay
     *     The inner {@link Broadcaster}.
     */
    void wrap(Broadcaster relay);
}
