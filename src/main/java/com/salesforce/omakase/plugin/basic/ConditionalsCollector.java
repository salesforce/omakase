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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.plugin.DependentPlugin;

import java.util.Set;

/**
 * Collects the set of all found conditions, i.e., the arguments inside of {@link ConditionalAtRuleBlock}s, from the {@link
 * Conditionals} plugin.
 * <p/>
 * This should be used when you want to determine which conditions are utilized within the source. This automatically enables the
 * {@link Conditionals} plugin.
 * <p/>
 * <b>Note</b>: The {@link Conditionals} plugin currently <em>lower-cases</em> all conditions for comparison purposes, so usually
 * any arguments passed to methods of this class should be lower-cased as well.
 *
 * @author nmcwilliams
 */
public final class ConditionalsCollector implements DependentPlugin {
    private final Set<String> conditions = Sets.newHashSet();

    @Override
    public void dependencies(PluginRegistry registry) {
        registry.require(Conditionals.class);
        registry.require(AutoRefiner.class).atRules();
    }

    /**
     * Subscription method - do not call directly.
     *
     * @param conditional
     *     The conditional at-rule block instance.
     */
    @Observe
    public void conditional(ConditionalAtRuleBlock conditional) {
        conditions.add(conditional.condition());
    }

    /**
     * Gets whether the given condition was found.
     *
     * @param condition
     *     Check if this condition was found. This should be lower-cased (unless you otherwise knowingly and explicitly arranged
     *     for parsed conditionals to <em>not</em> be automatically lower-cased in contrast to the default behavior).
     *
     * @return True if the given condition was found.
     */
    public boolean hasCondition(String condition) {
        return conditions.contains(condition);
    }

    /**
     * Returns a copy of the set of found conditions.
     *
     * @return The found conditions.
     */
    public ImmutableSet<String> foundConditions() {
        return ImmutableSet.copyOf(conditions);
    }
}
