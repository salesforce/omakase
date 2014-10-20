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

package com.salesforce.omakase.test.functional;

import com.google.common.collect.Sets;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.broadcast.AbstractBroadcaster;
import com.salesforce.omakase.broadcast.Broadcastable;

import java.util.Set;

import static org.fest.assertions.api.Assertions.fail;

/**
 * Utility for tests.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public final class StatusChangingBroadcaster extends AbstractBroadcaster {
    public final Set<Broadcastable> all = Sets.newHashSet();

    @Override
    public void broadcast(Broadcastable broadcastable) {
        if (all.contains(broadcastable)) {
            fail("unit shouldn't be broadcasted twice!");
        }
        all.add(broadcastable);
        broadcastable.status(Status.PROCESSED);
    }
}
