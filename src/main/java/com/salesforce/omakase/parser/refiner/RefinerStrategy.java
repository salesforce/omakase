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

package com.salesforce.omakase.parser.refiner;

import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.plugin.SyntaxPlugin;

/**
 * Represents a strategy for refining a {@link Refinable} object, such as a {@link Selector} or {@link Declaration}, or other
 * "refinable" syntax such as custom functions.
 * <p/>
 * This feature can be used to extend and customize the standard CSS syntax. See the readme file for more details.
 * <p/>
 * Library-standard CSS extensions (e.g ., conditionals) are implemented through this functionality as well.
 *
 * @author nmcwilliams
 * @see StandardRefinerStrategy
 * @see Refiner
 * @see SyntaxPlugin
 */
public interface RefinerStrategy {
}
