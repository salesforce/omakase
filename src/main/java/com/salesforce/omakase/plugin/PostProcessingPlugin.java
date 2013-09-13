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

import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;

/**
 * A {@link Plugin} that wishes to be notified when all processing is completed.
 *
 * @author nmcwilliams
 */
public interface PostProcessingPlugin extends Plugin {
    /**
     * This method will be called after all processing has completed (preprocessing, rework, and validation).
     * <p/>
     * This could be used when the {@link Plugin} must defer it's processing until it is certain that all {@link Selector}s and
     * {@link Declaration}s within the source are processed.
     * <p/>
     * The order in which this will be invoked (between plugins) is the same order that the {@link Plugin} was registered.
     *
     * @param registry
     *     The {@link PluginRegistry} instance.
     */
    void postProcess(PluginRegistry registry);
}
