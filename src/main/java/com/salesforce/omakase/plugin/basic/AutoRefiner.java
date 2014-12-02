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
 * <p/>
 * Generally this is used as a dependency in a {@link DependentPlugin}, when the {@link DependentPlugin} has a subscription to a
 * lower-level {@link Syntax} unit not exposed during the high-level parsing phase. The {@link Refinable} responsible for parsing
 * that syntax unit must be refined before the syntax unit will be exposed. Examples of lower-level {@link Syntax} units include
 * {@link ClassSelector} and {@link IdSelector}. For more information on auto-refinement see the readme file.
 * <p/>
 * Example:
 * <pre><code> public class MyPlugin implements DependentPlugin {
 *   {@literal @}Override public void before(PluginRegistry registry) {
 *     registry.require(AutoRefiner.class).declarations();
 *   }
 * <p/>
 *   ...(subscription methods)...
 * }<code></pre>
 * <p/>
 * If you are manually including this with {@link Omakase.Request#use(Plugin...)} then you almost always want to ensure that
 * it is registered  first before any other plugins.
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
