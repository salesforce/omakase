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

import com.google.common.collect.Sets;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.parser.refiner.RefinerRegistry;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.SyntaxPlugin;

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
