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

/**
 j,  * ADD LICENSE
 */
package com.salesforce.omakase.broadcaster;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Syntax;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TESTME
 * <p/>
 * A {@link Broadcaster} that will store all received broadcasted events. Replay the broadcasts using {@link #visit()}. The
 * broadcasts can be replayed multiple times.
 *
 * @author nmcwilliams
 */
public final class VisitingBroadcaster extends AbstractBroadcaster {
    private final List<Syntax> list = Lists.newArrayList();
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
    public <T extends Syntax> void broadcast(T syntax) {
        list.add(syntax);

        // while a visit is in progress, immediately send out any received broadcasts (can occur if a refinement results
        // in new syntax instances, or rework results in new syntax units being added).
        if (visiting) {
            relay.broadcast(syntax);
        }
    }

    /** Replays all broadcasted events. */
    public void visit() {
        visiting = true;

        // make a defensive copy since the list may be modified as a result of this call
        ImmutableList<Syntax> snapshot = ImmutableList.copyOf(list);

        for (Syntax syntax : snapshot) {
            relay.broadcast(syntax);
        }

        visiting = false;
    }
}
