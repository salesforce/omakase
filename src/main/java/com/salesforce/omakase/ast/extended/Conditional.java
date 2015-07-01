/*
 * Copyright (C) 2015 salesforce.com, inc.
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

package com.salesforce.omakase.ast.extended;

import com.salesforce.omakase.plugin.basic.Conditionals;
import com.salesforce.omakase.plugin.basic.ConditionalsConfig;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.Writable;

import java.io.IOException;

import static com.google.common.base.Preconditions.*;

/**
 * Represents a single condition within a {@link ConditionalAtRuleBlock}.
 *
 * @author nmcwilliams
 * @see Conditionals
 */
public class Conditional implements Writable {
    private final String condition;
    private final boolean isLogicalNegation;

    /**
     * Creates a new {@link Conditional} instance, with the given name and optional negation.
     *
     * @param condition
     *     The name of the condition, e.g., "ie7".
     * @param isLogicalNegation
     *     Specify true if the logical negation operator is present, e.g., "!ie7".
     */
    public Conditional(String condition, boolean isLogicalNegation) {
        this.condition = checkNotNull(condition, "condition cannot be null");
        this.isLogicalNegation = isLogicalNegation;
    }

    /**
     * Gets the name of the condition.
     *
     * @return The name of the condition.
     */
    public String condition() {
        return condition;
    }

    /**
     * Returns true if the logical negation operator is present for this condition. If present, it reverses the trueness value
     * during a match check.
     *
     * @return True if this condition is negated.
     */
    public boolean isLogicalNegation() {
        return isLogicalNegation;
    }

    /**
     * Checks whether this conditional is true based on the true conditions in the given ConditionalsConfig instance.
     * <p/>
     * If the given config contains a string condition matching this one, this method will return true, otherwise false. If {@link
     * #isLogicalNegation()} is true then this return value is reversed.
     *
     * @param config
     *     The config containing the set of true conditions.
     *
     * @return True if this conditional matches, otherwise false.
     */
    public boolean matches(ConditionalsConfig config) {
        return isLogicalNegation ? !config.hasCondition(condition) : config.hasCondition(condition);
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (isLogicalNegation) {
            appendable.append('!');
        }
        appendable.append(condition);
    }
}
