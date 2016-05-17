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

package com.salesforce.omakase.plugin.basic;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Automatically refines all explicitly requested {@link Refinable} types.
 * <p>
 * Generally this is used as a dependency in a {@link DependentPlugin}, when the {@link DependentPlugin} has a subscription to a
 * lower-level {@link Syntax} unit not exposed during the high-level parsing phase. The {@link Refinable} responsible for parsing
 * that syntax unit must be refined before the syntax unit will be exposed. Examples of lower-level {@link Syntax} units include
 * {@link ClassSelector} and {@link IdSelector}. For more information on auto-refinement see the readme file.
 * <p>
 * Example:
 * <pre><code> public class MyPlugin implements DependentPlugin {
 *   {@literal @}Override public void before(PluginRegistry registry) {
 *     registry.require(AutoRefiner.class).declarations();
 *   }
 *
 *   ...(subscription methods)...
 * }<code></pre>
 * <p>
 * If you are manually including this with {@link Omakase.Request#use(Plugin...)} then you almost always want to ensure that it is
 * registered  first before any other plugins.
 *
 * @author nmcwilliams
 */
public final class AutoRefiner implements Plugin {
    private final Set<Class<?>> refinables = new HashSet<>();
    private boolean all;

    /**
     * Specifies that all {@link AtRule}s should be automatically refined.
     *
     * @return this, for chaining.
     */
    public AutoRefiner atRules() {
        return include(AtRule.class);
    }

    /**
     * Specifies that all {@link Selector}s should be automatically refined.
     *
     * @return this, for chaining.
     */
    public AutoRefiner selectors() {
        return include(Selector.class);
    }

    /**
     * Specifies that all {@link Declaration}s should be automatically refined.
     *
     * @return this, for chaining.
     */
    public AutoRefiner declarations() {
        return include(Declaration.class);
    }

    /**
     * Includes the given class in auto-refinement. This means that {@link Refinable#refine()} will be automatically called on the
     * instance.
     *
     * @param klass
     *     The class to auto-refine.
     *
     * @return this, for chaining.
     */
    public AutoRefiner include(Class<? extends Refinable<?>> klass) {
        refinables.add(klass);
        return this;
    }

    /**
     * Specifies that <em>anything</em> that is {@link Refinable} should be automatically refined.
     *
     * @return this, for chaining.
     */
    public AutoRefiner all() {
        all = true;
        return this;
    }

    /**
     * Automatically refines anything that is refinable.
     *
     * @param refinable
     *     A refinable object.
     */
    @Rework
    public void refine(Refinable<?> refinable) {
        if (all || refinables.contains(refinable.getClass())) refinable.refine();
    }

    /**
     * Creates a new {@link AutoRefiner} that refines everything.
     *
     * @return The new {@link AutoRefiner} instance.
     */
    public static AutoRefiner refineEverything() {
        return new AutoRefiner().all();
    }
}
