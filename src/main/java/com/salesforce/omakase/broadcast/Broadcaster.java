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
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.broadcast.emitter.Emitter;

/**
 * Responsible for broadcasting {@link Broadcastable} events (usually newly created {@link Syntax} units).
 *
 * @author nmcwilliams
 * @see Emitter
 */
public interface Broadcaster {
    /**
     * Broadcasts the given event (syntax unit).
     *
     * @param broadcastable
     *     The {@link Broadcastable} syntax unit.
     */
    void broadcast(Broadcastable broadcastable);

    /**
     * Broadcasts the given event (syntax unit).
     * <p>
     * This will first call {@link #chain(Broadcaster)} on this broadcaster with the given broadcasters, so that all given
     * broadcasters will be at the end of the chain. Then the broadcast occurs. Afterwards all given broadcasters will be cut
     * from the chain.
     *
     * @param broadcastable
     *     The {@link Broadcastable} syntax unit.
     * @param first
     *     The first broadcaster to chain.
     * @param others
     *     Optional additional broadcasters to chain.
     */
    void chainBroadcast(Broadcastable broadcastable, Broadcaster first, Broadcaster... others);

    /**
     * Specifies an additional {@link Broadcaster} that should receive broadcasted events after this one has processed it.
     * <p>
     * If this {@link Broadcaster} is already relaying events to another one then chain will be called on that broadcaster
     * instead, all the way down the line until the given {@link Broadcaster} is at the bottom of the chain.
     * <p>
     * There are two main ways to use this method. The first way is that you create a new {@link Broadcaster} instance, either
     * passing the original broadcaster to the constructor or calling chain on the new broadcaster. Then you use your new
     * broadcaster in place of the old one. This puts your new broadcaster at the top of the chain. This way is the easiest, but
     * doesn't work in some scenarios.
     * <p>
     * The second way is that you call branch on the original broadcaster, passing it a newly created one. This results in the new
     * broadcaster being at the bottom of the chain instead. When doing it this way, you <em>must</em> call {@link
     * Broadcaster#cut(Broadcaster)} on the original broadcaster before your method returns, after you are finished with the
     * broadcasting. Otherwise your new broadcaster will be left dangling. However this way is <b>required</b> if the broadcasted
     * events you need pass through through a {@link Refine} plugin. Those methods will not use a broadcaster you place at the top
     * of the chain so being at the bottom is required. In this scenario prefer to use {@link #chainBroadcast(Broadcastable,
     * Broadcaster, Broadcaster...)}, which will take care of the cutting responsibility.
     *
     * @param broadcaster
     *     The inner {@link Broadcaster}.
     *
     * @return The same broadcaster instance given to it.
     */
    <T extends Broadcaster> T chain(T broadcaster);

    /**
     * Cuts the given {@link Broadcaster} from the chain so that it will no long receive additional events.
     *
     * @param broadcaster
     *     The broadcaster to cut.
     */
    void cut(Broadcaster broadcaster);
}
