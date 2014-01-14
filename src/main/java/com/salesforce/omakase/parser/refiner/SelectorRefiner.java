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
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.plugin.SyntaxPlugin;

/**
 * Represents a strategy for refining an {@link Selector} object. This allows you to add custom syntax with a structure similar to
 * standard selectors. This works in tandem with {@link SyntaxPlugin}s.
 *
 * @author nmcwilliams
 */
public interface SelectorRefiner extends Refiner {
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
     * Do <b>not</b> use anything on {@link Selector#parts()}, as that will result in infinite recursion!
     *
     * @param selector
     *     The {@link Selector} to refine.
     * @param broadcaster
     *     Used to broadcast any {@link Broadcastable} objects.
     * @param refiner
     *     Pass this refiner to any parser methods that require one.
     *
     * @return True if <em>complete</em> refinement was performed, otherwise false. If true, no other registered {@link
     *         Refiner} objects will be executed for the given instance. It is acceptable for a refiner to refine only a
     *         segment of the object and still return false.
     */
    boolean refine(Selector selector, Broadcaster broadcaster, GenericRefiner refiner);
}
