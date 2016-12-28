/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.broadcast.annotation;

import com.salesforce.omakase.ast.Named;
import com.salesforce.omakase.ast.RawFunction;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.plugin.syntax.DeclarationPlugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to subscribe to unrefined {@link Syntax} objects in order to provide or trigger refinement on them.
 * <p>
 * You can only subscribe to units that are {@link Refinable}, such as {@link RawFunction} and {@link Declaration}.
 * <p>
 * There are three required parameters. The first parameter is the {@link Refinable} syntax type. The second is of type {@link
 * Grammar}. The third is of type {@link Broadcaster}.
 * <p>
 * Use the {@link Grammar} instance to obtain sub-parsers and certain grammar tokens as necessary to assist with parsing. Using
 * {@link Source} may prove of benefit as well. Use the {@link Broadcaster} to broadcast all units that should be auto-associated.
 * For example, when refining a function you would broadcast one or more {@link Term}s. When refining a selector you would
 * broadcast one or more {@link SelectorPart}s. Note that when using a built-in parser broadcasting will happen automatically. If
 * you would like to refine a unit using the standard procedures then you can use one of the delegate methods on the appropriate
 * plugin, for example {@link DeclarationPlugin#delegateRefinement(Declaration, Grammar, Broadcaster)}. See the main readme file
 * for more information on custom refinement.
 * <p>
 * You can optionally add a name to this annotation to scope refinement to units with that name. For example,
 * <code><pre>
 * {@code @}Refine("myFunction")
 *  public void refine(RawFunction function, Grammar grammar, Broadcaster broadcaster}
 * </pre></code>
 * will only deliver {@link RawFunction}s with the name <em>myFunction</em>. Any {@link Refinable} that is an instance of {@link
 * Named} can be scoped in this manner. This matching is case-insensitive.
 * <p>
 * All {@link Refine} subscriptions will be delivered before {@link Rework} and {@link Validate}. Only perform refinement inside
 * of these methods, and prefer to broadcast units instead of directly attaching them to the subscribed unit. Once the subscribed
 * unit is refined ({@link Refinable#isRefined()}) then subsequent refiners will be skipped.
 *
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Refine {
    /**
     * Optionally specify a name to filter units. Only {@link Named} units with this name will be delivered.
     *
     * @return The filter name.
     */
    String value() default "";
}
