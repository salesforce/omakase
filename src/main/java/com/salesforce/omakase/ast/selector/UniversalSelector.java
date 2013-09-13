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

package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_SELECTOR;

/**
 * TESTME
 * <p/>
 * Represents the CSS universal selector, i.e., "*".
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "universal selector segment", broadcasted = REFINED_SELECTOR)
public class UniversalSelector extends AbstractSelectorPart implements SimpleSelector {
    /**
     * Constructs a new {@link UniversalSelector} instance.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     */
    public UniversalSelector(int line, int column) {
        super(line, column);
    }

    /** Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units). */
    public UniversalSelector() {}

    @Override
    public boolean isSelector() {
        return true;
    }

    @Override
    public boolean isCombinator() {
        return false;
    }

    @Override
    public SelectorPartType type() {
        return SelectorPartType.UNIVERSAL_SELECTOR;
    }

    @Override
    protected SelectorPart self() {
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (isDetached()) return;
        appendable.append('*');
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("abstract", super.toString())
            .toString();
    }
}
