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

import com.salesforce.omakase.ast.declaration.AbstractTerm;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.ast.declaration.TermView;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.plugin.SyntaxPlugin;

/**
 * Represents a strategy for refining an {@link RawFunction} object.
 * <p/>
 * This feature allows you to add custom functions. The refiner should read the data in the given {@link RawFunction} and create
 * and broadcast the applicable custom function object.
 * <p/>
 * Generally your custom function should implement {@link Term} or extend from {@link AbstractTerm}. If your custom function is a
 * collection or wrapper around inner {@link Term}s then you should implement {@link TermView}. See the readme file for more
 * details.
 * <p/>
 * This works in tandem with {@link SyntaxPlugin}s.
 *
 * @author nmcwilliams
 */
public interface FunctionRefiner extends RefinerStrategy {
    /**
     * Refines a {@link RawFunction}.
     * <p/>
     * The information in the given {@link RawFunction} can be used to determine if the function value is applicable to your
     * custom syntax. Most often you determine this based on the value from {@link RawFunction#name()}.
     * <p/>
     * Utilize the {@link RawFunction#args()} method to get the raw, unrefined args. Use this information to create your own
     * custom function object and then broadcast it using the given {@link Broadcaster} (if you intend to also subscribe to your
     * custom AST objects they must be annotated with the {@link Subscribable} annotation).
     * <p/>
     * <b>Important:</b> You must broadcast an object in order for this to work as expected. Specifically, you are expected to
     * broadcast one and <em>only one</em> object (e.g., your custom function instance) which implements {@link Term} or extends
     * from {@link AbstractTerm}.
     * <p/>
     * If your custom function is just a wrapper around a list of child terms then you must still create and broadcast a single
     * term object. In this case, your custom function should implement {@link TermView} to represent the inner list of terms that
     * will be written out. In other words, you cannot parse a {@link RawFunction} into directly broadcasted terms. Parse a single
     * custom object instead which contains those list of terms.
     *
     * @param raw
     *     The {@link RawFunction} that may potentially match your custom function.
     * @param broadcaster
     *     Used to broadcast any {@link Broadcastable} objects.
     * @param refiner
     *     Pass this refiner to any parser methods that require one.
     *
     * @return True if refinement was performed, otherwise false. If true, no other registered {@link RefinerStrategy} objects
     *         will be executed for the given {@link RawFunction} instance.
     */
    boolean refine(RawFunction raw, Broadcaster broadcaster, Refiner refiner);
}
