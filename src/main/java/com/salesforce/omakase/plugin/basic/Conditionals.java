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

import com.salesforce.omakase.parser.refiner.ConditionalRefinerStrategy;
import com.salesforce.omakase.parser.refiner.RefinerStrategy;
import com.salesforce.omakase.plugin.SyntaxPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO description
 *
 * @author nmcwilliams
 */
public final class Conditionals implements SyntaxPlugin {
    private final Set<String> trueConditions;

    public Conditionals() {
        this.trueConditions = new HashSet<>();
    }

    public Conditionals(Set<String> trueConditions) {
        this.trueConditions = new HashSet<>(trueConditions.size());
        addTrueConditions(trueConditions);
    }

    public Conditionals removeCondition(String condition) {
        trueConditions.remove(condition);
        return this;
    }

    public Conditionals clearTrueConditions() {
        trueConditions.clear();
        return this;
    }

    private Conditionals addTrueConditions(Iterable<String> trueConditions) {
        // add each condition, making sure it's lower-cased for comparison purposes
        for (String condition : trueConditions) {
            this.trueConditions.add(condition.toLowerCase());
        }

        return this;
    }

    @Override
    public RefinerStrategy getRefinableStrategy() {
        return new ConditionalRefinerStrategy(trueConditions);
    }
}
