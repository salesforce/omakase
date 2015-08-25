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

package com.salesforce.omakase.plugin.conditionals;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.extended.Conditional;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.error.ErrorLevel;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.DependentPlugin;

import java.util.Set;

/**
 * A validator plugin that will validate the correctness of the syntax and contents of conditional at-rules ({@link
 * ConditionalAtRuleBlock}). This works in tandem with the {@link Conditionals} plugin.
 * <p/>
 * You can optionally specify a set of allowedConditions. If specified, each condition in the CSS will be checked against this
 * set. If this check fails then an error will be reported.
 * <p/>
 * Please note that each allowedCondition will be automatically lower-cased for comparison purposes! This aligns with the
 * assumption of all other related conditionals plugins.
 * <p/>
 * Also note that this will automatically enable the {@link Conditionals} plugin, in passthroughMode (see {@link
 * ConditionalsConfig#passthroughMode(boolean)}) unless a {@link Conditionals} plugin instance was registered before this one.
 *
 * @author nmcwilliams
 * @see Conditionals
 * @see ConditionalsCollector
 * @see ConditionalsConfig
 * @see ConditionalAtRuleBlock
 */
public class ConditionalsValidator implements DependentPlugin {
    private static final String MSG = "Invalid condition '%s'. Must be one of '%s'";
    private final Set<String> allowedConditions;

    /**
     * Creates a new validator that will validate and refine the conditional at-rule blocks (syntax), but will <em>not</em> check
     * if the condition is allowed. In other words, this allows any condition to be specified.
     */
    public ConditionalsValidator() {
        allowedConditions = null;
    }

    /**
     * Creates a new validator that will validate and refine the conditional at-rule blocks (syntax), and will also confirm that
     * the condition is within the given set of allowed conditions. In other words, an error will be reported if a condition is
     * used in CSS that is not allowed.
     * <p/>
     * Please note that each allowedCondition will be automatically lower-cased for comparison purposes. This aligns with the
     * assumption of all other related conditionals plugins.
     *
     * @param allowedConditions
     *     The conditions that are allowed. Will be automatically lower-cased!
     */
    public ConditionalsValidator(String... allowedConditions) {
        this(Sets.newHashSet(allowedConditions));
    }

    /**
     * Creates a new validator that will validate and refine the conditional at-rule blocks, and will also confirm that the
     * condition is within the given set of allowed conditions.
     * <p/>
     * Please note that each allowedCondition will be automatically lower-cased for comparison purposes. This aligns with the
     * assumption of all other related conditionals plugins.
     *
     * @param allowedConditions
     *     The conditions that are allowed. Will be automatically lower-cased!
     */
    public ConditionalsValidator(Iterable<String> allowedConditions) {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (String condition : allowedConditions) {
            builder.add(condition.toLowerCase());
        }
        this.allowedConditions = builder.build();
    }

    @Override
    public void dependencies(PluginRegistry registry) {
        if (!registry.retrieve(Conditionals.class).isPresent()) {
            // if no explicit conditionals plugin was registered then it implies that only validation is desired,
            // which means we should enable passthroughMode.
            registry.register(new Conditionals(true));
        }
    }

    /**
     * Subscription method - do not call directly.
     *
     * @param block
     *     The block to validate.
     * @param em
     *     The error manager.
     */
    @Validate
    public void validate(ConditionalAtRuleBlock block, ErrorManager em) {
        for (Conditional conditional : block.conditionals()) {
            if (allowedConditions != null && !allowedConditions.contains(conditional.condition())) {
                em.report(ErrorLevel.FATAL, block, String.format(MSG, conditional.condition(), allowedConditions));
            }
        }
    }
}
