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

package com.salesforce.omakase.broadcast;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.emitter.Emitter;

/**
 * Responsible for broadcasting {@link Broadcastable} events (objects), usually newly created {@link Syntax} units.
 * <p/>
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
     * <p/>
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
