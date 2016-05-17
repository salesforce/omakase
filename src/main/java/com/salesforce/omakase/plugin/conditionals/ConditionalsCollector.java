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

package com.salesforce.omakase.plugin.conditionals;

import com.google.common.collect.ImmutableSet;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.extended.Conditional;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;

import java.util.HashSet;
import java.util.Set;

/**
 * Collects the set of all found conditions, i.e., the arguments inside of {@link ConditionalAtRuleBlock}s, from the {@link
 * Conditionals} plugin.
 * <p>
 * This should be used when you want to determine which conditions are utilized within the source. This automatically enables the
 * {@link Conditionals} plugin.
 * <p>
 * <b>Note</b>: The {@link Conditionals} plugin currently <em>lower-cases</em> all conditions for comparison purposes, so usually
 * any arguments passed to methods of this class should be lower-cased as well.
 *
 * @author nmcwilliams
 */
public final class ConditionalsCollector implements DependentPlugin {
    private final Set<String> conditions = new HashSet<>();
    private boolean excludeNegationOnly;

    @Override
    public void dependencies(PluginRegistry registry) {
        registry.require(Conditionals.class);
        registry.require(AutoRefiner.class).atRules();
    }

    /**
     * Specify true to ignore conditions that are <em>only</em> used with the negation operator (e.g., <code>!ie9</code>).
     * <p>
     * In some cases you may not care about conditions only referenced in this way. For example, if you were using this plugin to
     * determine which permutations to create based on which conditions are referenced, you wouldn't want the conditions only used
     * with negation, as you would not need to generate permutations for those conditions.
     * <p>
     * This method should only be called before parsing has begun.
     *
     * @param excludeNegationOnly
     *     Whether to ignore conditions only used with the negation operator.
     *
     * @return this, for chaining.
     */
    public ConditionalsCollector excludeNegationOnly(boolean excludeNegationOnly) {
        this.excludeNegationOnly = excludeNegationOnly;
        return this;
    }

    /**
     * Subscription method - do not call directly.
     *
     * @param block
     *     The conditional at-rule block instance.
     */
    @Observe
    public void conditional(ConditionalAtRuleBlock block) {
        for (Conditional c : block.conditionals()) {
            if (!excludeNegationOnly || !c.isLogicalNegation()) {
                conditions.add(c.condition());
            }
        }
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
