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
 * <p>
 * You can optionally specify a set of allowedConditions. If specified, each condition in the CSS will be checked against this
 * set. If this check fails then an error will be reported.
 * <p>
 * Please note that each allowedCondition will be automatically lower-cased for comparison purposes! This aligns with the
 * assumption of all other related conditionals plugins.
 * <p>
 * Also note that this will automatically enable the {@link Conditionals} plugin, in passthroughMode (see {@link
 * ConditionalsConfig#passthroughMode(boolean)}) unless a {@link Conditionals} plugin instance was registered before this one.
 *
 * @author nmcwilliams
 * @see Conditionals
 * @see ConditionalsCollector
 * @see ConditionalsConfig
 * @see ConditionalAtRuleBlock
 */
public final class ConditionalsValidator implements DependentPlugin {
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
     * <p>
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
     * <p>
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
     * Validation method.
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
