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
import com.salesforce.omakase.ast.declaration.value.FunctionValue;
import com.salesforce.omakase.ast.declaration.value.RefinedFunctionValue;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.plugin.SyntaxPlugin;

/**
 * Represents a strategy for refining an {@link FunctionValue} object. This allows you to add custom functions. This works in
 * tandem with {@link SyntaxPlugin}s.
 *
 * @author nmcwilliams
 */
public interface FunctionValueRefinerStrategy extends RefinerStrategy {
    /**
     * Refines a {@link FunctionValue}.
     * <p/>
     * The information in the given {@link FunctionValue} can be used to determine if the function value is applicable to your
     * custom syntax. Most often you determine this based on the value from {@link FunctionValue#name()}.
     * <p/>
     * Utilize the {@link FunctionValue#args()} method to get the raw, unrefined args. Use this information to create your own
     * custom {@link RefinedFunctionValue} object and then optionally broadcast them using the given {@link Broadcaster} (if you
     * intend to broadcast your custom AST objects they must be annotated with the {@link Subscribable} annotation and implement
     * {@link Syntax}). Be sure to actually add the objects to the {@link FunctionValue} using the {@link
     * FunctionValue#refinedValue(RefinedFunctionValue)} method.
     *
     * @param functionValue
     *     The {@link FunctionValue} to refine.
     * @param broadcaster
     *     Used to broadcast any {@link Broadcastable} objects.
     * @param refiner
     *     Pass this refiner to any parser methods that require one.
     *
     * @return True if refinement was performed, otherwise false. If true, no other registered {@link RefinerStrategy} objects
     *         will be executed for the given {@link AtRule} instance.
     */
    boolean refine(FunctionValue functionValue, Broadcaster broadcaster, Refiner refiner);
}
