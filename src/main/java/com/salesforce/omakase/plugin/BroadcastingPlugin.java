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

package com.salesforce.omakase.plugin;

import com.salesforce.omakase.broadcast.Broadcaster;

/**
 * A plugin that needs access to a {@link Broadcaster}.
 * <p/>
 * In most cases this is not the type of plugin that you want for customized (non-library-provided) plugins. Broadcasting your own
 * events may result in unexpected behavior.
 *
 * @author nmcwilliams
 */
public interface BroadcastingPlugin extends Plugin {
    /**
     * This method will be called with a reference to the {@link Broadcaster} for the current parsing operation.
     *
     * @param broadcaster
     *     The {@link Broadcaster} instance.
     */
    void broadcaster(Broadcaster broadcaster);
}
