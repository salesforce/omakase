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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_SELECTOR;

/**
 * TESTME
 * <p/>
 * Represents a CSS type selector (also known as an element type selector).
 * <p/>
 * Do not use this for universal "*" selectors, but use {@link UniversalSelector} instead.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "type/element selector segment", broadcasted = REFINED_SELECTOR)
public class TypeSelector extends AbstractSelectorPart implements SimpleSelector {
    private String name;

    /**
     * Constructs a new {@link TypeSelector} instance with the given name.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param name
     *     Name of the element / type.
     */
    public TypeSelector(int line, int column, String name) {
        super(line, column);
        this.name = name.toLowerCase();
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param name
     *     Name of the element / type.
     */
    public TypeSelector(String name) {
        name(name);
    }

    /**
     * Sets the name.
     *
     * @param name
     *     The element name.
     *
     * @return this, for chaining.
     */
    public TypeSelector name(String name) {
        checkNotNull(name, "name cannot be null");
        this.name = name.toLowerCase();
        return this;
    }

    /**
     * Gets the name of the selector.
     *
     * @return The name.
     */
    public String name() {
        return name;
    }

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
        return SelectorPartType.TYPE_SELECTOR;
    }

    @Override
    protected SelectorPart self() {
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (isDetached()) return;
        appendable.append(name);
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("syntax", super.toString())
            .add("name", name)
            .toString();
    }
}
