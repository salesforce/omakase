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

package com.salesforce.omakase.ast.extended;

import com.salesforce.omakase.plugin.conditionals.Conditionals;
import com.salesforce.omakase.plugin.conditionals.ConditionalsConfig;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.Writable;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

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
     * <p>
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
