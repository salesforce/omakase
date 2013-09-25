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

import com.salesforce.omakase.parser.RefinableStrategy;

/**
 * A {@link Plugin} that registers a custom {@link RefinableStrategy}.
 * <p/>
 * The {@link RefinableStrategy} can be used to customize and extends the standard CSS syntax. See the readme file for more
 * details and examples.
 *
 * @author nmcwilliams
 */
public interface SyntaxPlugin extends Plugin {
    /**
     * Gets the {@link RefinableStrategy} instance.
     *
     * @return The {@link RefinableStrategy} instance.
     */
    RefinableStrategy getRefinableStrategy();
}
