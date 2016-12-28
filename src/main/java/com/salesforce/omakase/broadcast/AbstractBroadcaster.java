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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base {@link Broadcaster} class.
 *
 * @author nmcwilliams
 */
public abstract class AbstractBroadcaster implements Broadcaster {
    /** next {@link Broadcaster} in the chain */
    protected Broadcaster next;

    @Override
    public void chainBroadcast(Broadcastable broadcastable, Broadcaster first, Broadcaster... broadcasters) {
        checkNotNull(first, "broadcaster cannot be null");

        chain(first);

        for (Broadcaster broadcaster : broadcasters) {
            first.chain(broadcaster);
        }

        broadcast(broadcastable);
        cut(first);
    }

    @Override
    public <T extends Broadcaster> T chain(T broadcaster) {
        if (next != null) {
            return next.chain(broadcaster);
        } else {
            this.next = broadcaster;
            return broadcaster;
        }
    }

    @Override
    public void cut(Broadcaster broadcaster) {
        if (next == broadcaster) {
            next = null;
        } else if (next != null) {
            next.cut(broadcaster);
        }
    }

    /**
     * Relays events down the chain to the next {@link Broadcaster}, if one was given via
     * {@link Broadcaster#chain(Broadcaster)}.
     *
     * @param broadcastable
     *     The event.
     */
    protected void relay(Broadcastable broadcastable) {
        if (next != null) {
            next.broadcast(broadcastable);
        }
    }
}
