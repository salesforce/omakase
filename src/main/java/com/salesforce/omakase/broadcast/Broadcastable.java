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

import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.emitter.Emitter;

/**
 * Something that can be broadcasted via an {@link Emitter}.
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
     * itself is broadcasted, this method should be called on the parent unit in order to propagate the broadcast event to the
     * children, ensuring that each child unit is properly broadcasted as well.
     *
     * @param broadcaster
     *     Use this {@link Broadcaster} to broadcast all unbroadcasted child units.
     *
     * @see Broadcaster#broadcast(Broadcastable, boolean)
     */
    void propagateBroadcast(Broadcaster broadcaster);
}
