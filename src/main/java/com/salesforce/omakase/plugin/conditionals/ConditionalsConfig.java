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

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.salesforce.omakase.util.As;

/**
 * Manages the set of "trueConditions" as used by the {@link Conditionals} plugin and individual {@link ConditionalAtRuleBlock}
 * objects, and other options.
 * <p>
 * This can be used to add to or remove from the set of "trueConditions". You can also specify "passthroughMode" as true, which
 * indicates that the {@link ConditionalAtRuleBlock} should write itself out without stripping the actual outer block
 * (<code>@if(...) { ... }</code>) away. This might be useful if you intend to deal with the conditionals as part of a subsequent
 * parsing operation.
 *
 * @author nmcwilliams
 */
public final class ConditionalsConfig {
    private final Set<String> trueConditions = new HashSet<>();
    private boolean passthroughMode;

    /**
     * Gets whether the given condition is "true".
     *
     * @param condition
     *     Check if this condition is contained within the "trueConditions" set. This should be lower-cased!
     *
     * @return True if the given condition is "true".
     */
    public boolean hasCondition(String condition) {
        return trueConditions.contains(condition);
    }

    /**
     * Returns an immutable copy of the trueConditions set.
     *
     * @return An immutable copy of the trueConditions set.
     */
    public ImmutableSet<String> trueConditions() {
        return ImmutableSet.copyOf(trueConditions);
    }

    /**
     * Adds the given strings to the trueConditions set. Each string will be automatically lower-cased for comparison purposes.
     *
     * @param trueConditions
     *     The strings that should evaluate to "true".
     *
     * @return this, for chaining.
     */
    public ConditionalsConfig addTrueConditions(String... trueConditions) {
        return addTrueConditions(ImmutableSet.copyOf(trueConditions));
    }

    /**
     * Adds the given strings to the trueConditions set. Each string will be automatically lower-cased for comparison purposes.
     *
     * @param trueConditions
     *     Iterable of the strings that should evaluate to "true".
     *
     * @return this, for chaining.
     */
    public ConditionalsConfig addTrueConditions(Iterable<String> trueConditions) {
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
    public ConditionalsConfig removeTrueCondition(String condition) {
        trueConditions.remove(condition);
        return this;
    }

    /**
     * Removes all current trueConditions.
     *
     * @return this, for chaining.
     */
    public ConditionalsConfig clearTrueConditions() {
        trueConditions.clear();
        return this;
    }

    /**
     * Removes the currently set true conditions and adds the given true conditions. Each string will be automatically lower-cased
     * for comparison purposes.
     *
     * @param trueConditions
     *     Replace all current true conditions with these.
     *
     * @return this, for chaining.
     */
    public ConditionalsConfig replaceTrueConditions(String... trueConditions) {
        return replaceTrueConditions(ImmutableSet.copyOf(trueConditions));
    }

    /**
     * Removes the currently set true conditions and adds the given true conditions. Each string will be automatically lower-cased
     * for comparison purposes.
     *
     * @param trueConditions
     *     Replace all current true conditions with these.
     *
     * @return this, for chaining.
     */
    public ConditionalsConfig replaceTrueConditions(Iterable<String> trueConditions) {
        clearTrueConditions();
        return addTrueConditions(trueConditions);
    }

    /**
     * Sets the passthroughMode status.
     *
     * @param passthroughMode
     *     Whether passthroughMode is true or false.
     *
     * @return this, for chaining.
     */
    public ConditionalsConfig passthroughMode(boolean passthroughMode) {
        this.passthroughMode = passthroughMode;
        return this;
    }

    /**
     * Gets whether passthroughMode is true or false.
     *
     * @return Whether passthroughMode is true or false.
     */
    public boolean isPassthroughMode() {
        return passthroughMode;
    }

    @Override
    public String toString() {
        return As.string(this).fields().toString();
    }
}
