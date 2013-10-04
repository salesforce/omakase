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
import com.salesforce.omakase.parser.refiner.ConditionalRefinerStrategy;
import com.salesforce.omakase.parser.refiner.RefinerStrategy;
import com.salesforce.omakase.plugin.SyntaxPlugin;

import java.util.HashSet;
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
 * Conditionals conditionals = new Conditionals(Sets.newHashSet("ie7"));
 * Omakase.source(input).request(conditionals).process();
 * </pre>
 * <p/>
 * For more information on using and configuring conditionals see the main readme file.
 *
 * @author nmcwilliams
 */
public final class Conditionals implements SyntaxPlugin {
    private final Set<String> trueConditions;

    /**
     * Creates a new {@link Conditionals} plugin instance with no specified true conditions. Be sure to add the conditions later
     * if applicable.
     */
    public Conditionals() {
        this.trueConditions = new HashSet<>();
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
        this.trueConditions = new HashSet<>(trueConditions.size());
        addTrueConditions(trueConditions);
    }

    /**
     * Adds the given strings to the trueConditions set. Each string will be automatically lower-cased for comparison purposes.
     *
     * @param trueConditions
     *     Iterable of the strings that should evaluate to "true".
     *
     * @return this, for chaining.
     */
    public Conditionals addTrueConditions(Iterable<String> trueConditions) {
        // add each condition, making sure it's lower-cased for comparison purposes
        for (String condition : trueConditions) {
            this.trueConditions.add(condition.toLowerCase());
        }

        return this;
    }

    /**
     * Removes the given condition from the trueConditions set. This condition will no longer evaluate as "true".
     *
     * @param condition
     *     The condition to remove.
     *
     * @return this, for chaining.
     */
    public Conditionals removeCondition(String condition) {
        trueConditions.remove(condition);
        return this;
    }

    /**
     * Removes all current trueConditions.
     *
     * @return this, for chaining.
     */
    public Conditionals clearTrueConditions() {
        trueConditions.clear();
        return this;
    }

    /**
     * Returns an immutable copy of the trueConditions set.
     *
     * @return An immutable copy of the trueConditions set.
     */
    public ImmutableSet<String> trueConditions() {
        return ImmutableSet.copyOf(trueConditions);
    }

    @Override
    public RefinerStrategy getRefinableStrategy() {
        return new ConditionalRefinerStrategy(trueConditions);
    }
}
