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

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.plugin.SyntaxPlugin;

/**
 * Represents a strategy for refining an {@link Declaration} object. This allows you to add custom syntax with a structure similar
 * to standard declarations. This works in tandem with {@link SyntaxPlugin}s.
 *
 * @author nmcwilliams
 */
public interface DeclarationRefiner extends RefinerStrategy {
    /**
     * Refines a {@link Declaration}.
     * <p/>
     * The information in the given {@link Declaration} can be used to determine if the declaration is applicable to your custom
     * syntax (e.g., checking {@link Declaration#rawPropertyValue()}.
     * <p/>
     * Utilize the {@link Declaration#rawPropertyValue()} to get the raw, unrefined property value syntax. Note that it is not
     * expected for you to refine the property name, although you can do that if you check the {@link
     * Declaration#rawPropertyName()} method and set the {@link Declaration#propertyName(PropertyName)} as appropriate.
     * <p/>
     * Parse the information into your own {@link PropertyValue} object and then optionally broadcast it using the given {@link
     * Broadcaster} (if you intend to broadcast your custom AST objects they must be annotated with the {@link Subscribable}
     * annotation and implement {@link Syntax}). Be sure to actually apply the new object using the {@link Declaration
     * #propertyValue(PropertyValue)} method.
     *
     * @param declaration
     *     The {@link Declaration} to refine.
     * @param broadcaster
     *     Used to broadcast any {@link Broadcastable} objects.
     * @param refiner
     *     Pass this refiner to any parser methods that require one.
     *
     * @return True if <em>complete</em> refinement was performed, otherwise false. If true, no other registered {@link
     *         RefinerStrategy} objects will be executed for the given instance. It is acceptable for a refiner to refine only a
     *         segment of the object and still return false.
     */
    boolean refine(Declaration declaration, Broadcaster broadcaster, Refiner refiner);
}
