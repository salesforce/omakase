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
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.AtRuleBlock;
import com.salesforce.omakase.ast.atrule.AtRuleExpression;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.plugin.SyntaxPlugin;

/**
 * Represents a strategy for refining an {@link AtRule} object. This allows you to add custom syntax with a structure similar to
 * standard at-rules. This works in tandem with {@link SyntaxPlugin}s.
 *
 * @author nmcwilliams
 */
public interface AtRuleRefinerStrategy extends RefinerStrategy {
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
     * <p/>
     * If the actual at-rule name (e.g., "@media")should be discarded then call {@link AtRule#shouldWriteName(boolean)} with
     * false.
     *
     * @param atRule
     *     The {@link AtRule} to refine.
     * @param broadcaster
     *     Used to broadcast any {@link Broadcastable} objects.
     * @param refiner
     *     Pass this refiner to any parser methods that require one.
     *
     * @return True if refinement was performed, otherwise false. If true, no other registered {@link RefinerStrategy} objects
     *         will be executed for the given {@link AtRule} instance.
     */
    boolean refine(AtRule atRule, Broadcaster broadcaster, Refiner refiner);
}
