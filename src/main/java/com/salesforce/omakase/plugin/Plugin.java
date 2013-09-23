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

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

/**
 * A plugin that is registered during CSS processing to perform rework, validation, and more.
 * <p/>
 * Plugins are registered during parser setup using {@link Omakase.Request#request(Plugin...)} (and similar methods). Plugins will
 * generally be executed in the order that they are registered.
 * <p/>
 * Note that when implementing a plugin, not all subscriptions will be received automatically. sometimes an {@link AutoRefiner} or
 * a {@link SyntaxTree} is needed.
 * <p/>
 * Subscription method invocation order follows this pattern:
 * <p/>
 * {@code @}PreProcess -> {@code @}Rework/{@code @}Observe -> {@code @}Validate.
 * <p/>
 * In a class hierarchy, the more specific type is received before the more abstract type (e.g., {@link ClassSelector}
 * subscription methods invoked before {@link Syntax} subscription methods).
 * <p/>
 * For much more information on utilizing or creating plugins please see the main readme file.
 *
 * @author nmcwilliams
 */
public interface Plugin {
}
