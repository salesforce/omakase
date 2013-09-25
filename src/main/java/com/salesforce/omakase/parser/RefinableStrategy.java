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

package com.salesforce.omakase.parser;

import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.AtRuleBlock;
import com.salesforce.omakase.ast.atrule.AtRuleExpression;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.ast.declaration.value.PropertyValue;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.plugin.SyntaxPlugin;

/**
 * Represents a strategy for refining a {@link Refinable} object, such as a {@link Selector} or {@link Declaration}.
 * <p/>
 * This can be used to extend and customize the standard CSS syntax. See the readme file for more details.
 * <p/>
 * Library-standard CSS extensions (e.g ., conditionals) are implemented through this functionality as well.
 *
 * @author nmcwilliams
 * @see StandardRefinableStrategy
 * @see Refiner
 * @see SyntaxPlugin
 */
public interface RefinableStrategy {
    /**
     * Refines an {@link AtRule}.
     * <p/>
     * The information in the given {@link AtRule} can be used to determine if the at-rule is applicable to your custom syntax.
     * Most often you determine this based on the value from {@link AtRule#name()}.
     * <p/>
     * Utilize the {@link AtRule#rawExpression()} and {@link AtRule#rawBlock()} methods to get the raw, unrefined syntax. Parse
     * this information into your own {@link AtRuleExpression} and {@link AtRuleBlock} objects and then optionally broadcast them
     * using the given {@link Broadcaster} (if you intend to broadcast your custom AST objects they must be annotated with the
     * {@link Subscribable} annotation and implement {@link Syntax}). Be sure to actually add the objects to the {@link AtRule}
     * using the {@link AtRule#expression(AtRuleExpression)} and {@link AtRule#block(AtRuleBlock)} methods. One or both of these
     * methods should be called (i.e., it's fine if your customized object does not have both).
     *
     * @param atRule
     *     The {@link AtRule} to refine.
     * @param broadcaster
     *     Used to broadcast any {@link Broadcastable} objects.
     *
     * @return True if refinement was performed, otherwise false. If true, no other registered {@link RefinableStrategy} objects
     *         will be executed for the given {@link AtRule} instance.
     */
    boolean refineAtRule(AtRule atRule, Broadcaster broadcaster);

    /**
     * Refines a {@link Selector}.
     * <p/>
     * The information in the given {@link Selector} can be used to determine if the selector is applicable to your custom syntax
     * (e.g., checking {@link Selector#rawContent()} or even {@link Selector#comments()}).
     * <p/>
     * Utilize the {@link Selector#rawContent()} to get the raw, unrefined syntax. Note that it's possible for this content to
     * contain comments. Parse this information into your own custom {@link SelectorPart} objects and then optionally broadcast
     * them using the given {@link Broadcaster} (if you intend to broadcast your custom AST objects they must be annotated with
     * the {@link Subscribable} annotation and implement {@link Syntax}). Be sure to actually add the objects to the {@link
     * Selector} by using the {@link Selector#appendAll(Iterable)} method.
     * <p/>
     * Do <b>not</b> use anything on {@link Selector#parts()}, as that will result in an infinite loop!
     *
     * @param selector
     *     The {@link Selector} to refine.
     * @param broadcaster
     *     Used to broadcast any {@link Broadcastable} objects.
     *
     * @return True if refinement was performed, otherwise false. If true, no other registered {@link RefinableStrategy} objects
     *         will be executed for the given {@link Selector} instance.
     */
    boolean refineSelector(Selector selector, Broadcaster broadcaster);

    /**
     * Refines a {@link Declaration}.
     * <p/>
     * The information in the given {@link Declaration} can be used to determine if the selector is applicable to your custom
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
     *
     * @return True if refinement was performed, otherwise false. If true, no other registered {@link RefinableStrategy} objects
     *         will be executed for the given {@link Declaration} instance.
     */
    boolean refineDeclaration(Declaration declaration, Broadcaster broadcaster);
}
