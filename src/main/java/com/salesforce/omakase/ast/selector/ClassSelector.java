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

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.selector.ClassSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_SELECTOR;

/**
 * Represents a CSS class selector.
 *
 * @author nmcwilliams
 * @see ClassSelectorParser
 */
@Subscribable
@Description(value = "class selector segment", broadcasted = REFINED_SELECTOR)
public final class ClassSelector extends AbstractSelectorPart implements SimpleSelector {
    private String name;

    /**
     * Creates a new instance with the given line and column numbers.
     * <p/>
     * If dynamically creating a new instance then use {@link #ClassSelector(String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param name
     *     The name of the class.
     */
    public ClassSelector(int line, int column, String name) {
        super(line, column);
        this.name = name;
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param name
     *     The name of the class.
     */
    public ClassSelector(String name) {
        name(name);
    }

    /**
     * Sets the class name.
     *
     * @param name
     *     The new class name
     *
     * @return this, for chaining.
     */
    public ClassSelector name(String name) {
        this.name = checkNotNull(name, "name cannot be null");
        return this;
    }

    /**
     * Gets the class name.
     *
     * @return The class name.
     */
    public String name() {
        return name;
    }

    @Override
    public SelectorPartType type() {
        return SelectorPartType.CLASS_SELECTOR;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append('.').append(name);
    }

    @Override
    public ClassSelector copy() {
        return new ClassSelector(name).copiedFrom(this);
    }
}
