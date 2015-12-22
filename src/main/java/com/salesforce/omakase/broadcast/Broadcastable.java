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

/**
 * Something that can be broadcasted.
 * <p/>
 * The most common {@link Broadcastable} is {@link Syntax}. {@link Syntax} units are usually broadcasted upon creation.
 *
 * @author nmcwilliams
 * @see Broadcaster
 * @see EmittingBroadcaster
 */
public interface Broadcastable {
    /**
     * Sets the current broadcast status. For internal use only, <strong>do not call directly</strong>.
     *
     * @param status
     *     The new status.
     */
    void status(Status status);

    /**
     * Gets the current broadcast status of this unit.
     * <p/>
     * This primarily determines whether this unit should be broadcasted again, given that each unit should be broadcasted at most
     * once per phase.
     *
     * @return The current broadcast status.
     */
    Status status();

    /**
     * Broadcasts all unbroadcasted child units using the given {@link Broadcaster}.
     * <p/>
     * This is primarily used for dynamically created {@link Syntax} units that have child or inner units. When the parent unit
     * itself is broadcasted, this method should be called on the parent unit in o
     * <p/>
     * Implementers, generally speaking, should call {@link #propagateBroadcast(Broadcaster)} on child units before propagating
     * itself, to match the broadcasting order of parsed units.
     *
     * @param broadcaster
     *     Use this {@link Broadcaster} to broadcast all unbroadcasted child units.
     *
     * @see Broadcaster#broadcast(Broadcastable, boolean)
     */
    void propagateBroadcast(Broadcaster broadcaster);
}
