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

import com.salesforce.omakase.util.As;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_SELECTOR;

/**
 * Represents the CSS universal selector, i.e., "*".
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "universal selector segment", broadcasted = REFINED_SELECTOR)
public final class UniversalSelector extends AbstractSelectorPart implements SimpleSelector {
    /**
     * Constructs a new {@link UniversalSelector} instance.
     * <p/>
     * If dynamically creating a new instance then use {@link #UniversalSelector()} instead.
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
    public SelectorPartType type() {
        return SelectorPartType.UNIVERSAL_SELECTOR;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append('*');
    }

    @Override
    public String toString() {
        return As.string(this).indent().add("abstract", super.toString()).toString();
    }
}
