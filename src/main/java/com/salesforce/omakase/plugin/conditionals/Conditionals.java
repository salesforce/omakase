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

import com.google.common.collect.Sets;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.parser.refiner.RefinerRegistry;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.SyntaxPlugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;

import java.util.Set;

/**
 * An extension to the standard CSS syntax that allows for conditional at-rules.
 * <p/>
 * Example of a conditional at-rule:
 * <pre>
 * {@code @}if(ie7) { .test{color:red} }
 * </pre>
 * <p/>
 * This block will output its inner statements if its condition matches one of the true conditions in the {@link
 * ConditionalsConfig} instance.
 * <p/>
 * To enable conditionals, register an instance of this plugin during parser setup:
 * <pre>
 * Conditionals conditionals = new Conditionals("ie7");
 * Omakase.source(input).use(conditionals).process();
 * </pre>
 * <p/>
 * For more information on using and configuring conditionals see the main readme file.
 *
 * @author nmcwilliams
 * @see ConditionalsConfig
 * @see ConditionalsCollector
 */
public final class Conditionals implements SyntaxPlugin, DependentPlugin {
    private final ConditionalsConfig config = new ConditionalsConfig();

    /**
     * Creates a new {@link Conditionals} plugin instance with no specified true conditions. Be sure to add these true conditions
     * later via the {@link #config()} method if applicable.
     */
    public Conditionals() {}

    /**
     * Creates a new {@link Conditionals} plugin instance with passthroughMode set as given. See {@link
     * ConditionalsConfig#passthroughMode(boolean)} for more information. Be sure to add the conditions later via the {@link
     * #config()} method if applicable.
     *
     * @param passthroughMode
     *     Whether passthroughMode should be enabled.
     */
    public Conditionals(boolean passthroughMode) {
        config.passthroughMode(passthroughMode);
    }

    /**
     * Creates a new {@link Conditionals} plugin instance with the given list of true conditions. Each string in the set will be
     * automatically lower-cased for comparison purposes.
     *
     * @param trueConditions
     *     List of the strings that should evaluate to "true".
     */
    public Conditionals(String... trueConditions) {
        this(Sets.newHashSet(trueConditions));
    }

    /**
     * Creates a new {@link Conditionals} plugin instance with the given list of true conditions. Each string in the set will be
     * automatically lower-cased for comparison purposes.
     *
     * @param trueConditions
     *     Set containing the strings that should evaluate to "true".
     */
    public Conditionals(Set<String> trueConditions) {
        config.addTrueConditions(trueConditions);
    }

    @Override
    public void dependencies(PluginRegistry registry) {
        registry.require(AutoRefiner.class).atRules();
    }

    @Override
    public void registerRefiners(RefinerRegistry registry) {
        registry.register(new ConditionalsRefiner(config));
    }

    /**
     * Gets the {@link ConditionalsConfig} instance. The {@link ConditionalsConfig} can be used to add, remove, or update the set
     * of "trueConditions".
     *
     * @return The {@link ConditionalsConfig} instance.
     */
    public ConditionalsConfig config() {
        return config;
    }
}
