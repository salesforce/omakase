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

import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;

/**
 * Notification event that a stylesheet has ended.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description("the end of a stylesheet")
public final class NotifyStylesheetEnd extends AbstractNotification {
    private static final NotifyStylesheetEnd INSTANCE = new NotifyStylesheetEnd();

    private NotifyStylesheetEnd() {}

    /**
     * Gets the singleton instance of this class.
     *
     * @param broadcaster
     *     Broadcast to this {@link Broadcaster}.
     */
    public static void broadcast(Broadcaster broadcaster) {
        broadcaster.broadcast(INSTANCE);
    }
}
