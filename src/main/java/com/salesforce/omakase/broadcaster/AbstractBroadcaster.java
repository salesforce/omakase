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

package com.salesforce.omakase.broadcaster;

import com.salesforce.omakase.ast.Syntax;

/**
 * Base {@link Broadcaster} class.
 *
 * @author nmcwilliams
 */
public abstract class AbstractBroadcaster implements Broadcaster {
    /** inner {@link Broadcaster} */
    protected Broadcaster relay;

    @Override
    public <T extends Syntax> void broadcast(T syntax, boolean propagate) {
        broadcast(syntax);
        if (propagate) syntax.propagateBroadcast(this);
    }

    @Override
    public Broadcaster wrap(Broadcaster relay) {
        this.relay = relay;
        return this;
    }

    /**
     * Gets whether this {@link Broadcaster} is wrapped around a child {@link Broadcaster}.
     *
     * @return True if there is an inner {@link Broadcaster} specified.
     */
    protected boolean hasRelay() {
        return relay != null;
    }
}
