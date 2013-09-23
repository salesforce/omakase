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

package com.salesforce.omakase.notification;

import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;

/**
 * Base class for notification-only objects.
 *
 * @author nmcwilliams
 */
class AbstractNotification implements Broadcastable {
    @Override
    public void status(Status status) {}

    @Override
    public Status status() {
        return Status.UNBROADCASTED; // repeated broadcasts are fine
    }

    @Override
    public void broadcaster(Broadcaster broadcaster) {}

    @Override
    public Broadcaster broadcaster() {
        throw new UnsupportedOperationException("unsupported operation on a notification-only object.");
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        throw new UnsupportedOperationException("unsupported operation on a notification-only object.");
    }
}
