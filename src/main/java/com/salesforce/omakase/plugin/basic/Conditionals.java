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
import com.salesforce.omakase.parser.refiner.ConditionalsRefiner;
import com.salesforce.omakase.parser.refiner.RefinerStrategy;
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
 * This block will output its inner statements if its condition (argument) is contained within a specified set of strings that
 * should evaluate to "true".
 * <p/>
 * To enable conditionals, register an instance of this plugin during parser setup:
 * <pre>
 * Conditionals conditionals = new Conditionals("ie7");
 * Omakase.source(input).request(conditionals).process();
 * </pre>
 * <p/>
 * For more information on using and configuring conditionals see the main readme file.
 *
 * @author nmcwilliams
 * @see ConditionalsManager
 * @see ConditionalsCollector
 */
public final class Conditionals implements SyntaxPlugin, DependentPlugin {
    private final ConditionalsManager manager = new ConditionalsManager();

    /**
     * Creates a new {@link Conditionals} plugin instance with no specified true conditions. Be sure to add the conditions later
     * via the {@link #manager()} method if applicable.
     */
    public Conditionals() {}

    /**
     * Creates a new {@link Conditionals} plugin instance with passthroughMode set as given. See {@link
     * ConditionalsManager#passthroughMode(boolean)} for more information. Be sure to add the conditions later via the {@link
     * #manager()} method if applicable.
     *
     * @param passthroughMode
     *     Whether passthroughMode should be enabled.
     */
    public Conditionals(boolean passthroughMode) {
        manager.passthroughMode(passthroughMode);
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
        manager.addTrueConditions(trueConditions);
    }

    @Override
    public void dependencies(PluginRegistry registry) {
        registry.require(AutoRefiner.class).atRules();
    }

    @Override
    public RefinerStrategy getRefinableStrategy() {
        return new ConditionalsRefiner(manager);
    }

    /**
     * Gets the {@link ConditionalsManager} instance. The {@link ConditionalsManager} can be used to add, remove, or update the
     * set of "trueConditions".
     *
     * @return The {@link ConditionalsManager} instance.
     */
    public ConditionalsManager manager() {
        return manager;
    }
}
