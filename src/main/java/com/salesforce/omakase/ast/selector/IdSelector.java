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

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.selector.IdSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_SELECTOR;

/**
 * Represents a CSS id selector.
 *
 * @author nmcwilliams
 * @see IdSelectorParser
 */
@Subscribable
@Description(value = "id selector segment", broadcasted = REFINED_SELECTOR)
public final class IdSelector extends AbstractSelectorPart implements SimpleSelector {
    private String name;

    /**
     * Creates a new instance with the given line and column numbers and id name.
     * <p/>
     * If dynamically creating a new instance then use {@link #IdSelector(String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param name
     *     The id name.
     */
    public IdSelector(int line, int column, String name) {
        super(line, column);
        this.name = name;
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param name
     *     The id name.
     */
    public IdSelector(String name) {
        name(name);
    }

    /**
     * Sets the id name.
     *
     * @param name
     *     The id name.
     *
     * @return this, for chaining.
     */
    public IdSelector name(String name) {
        this.name = checkNotNull(name, "name cannot be null");
        return this;
    }

    /**
     * Gets the id name.
     *
     * @return The id name.
     */
    public String name() {
        return name;
    }

    @Override
    public SelectorPartType type() {
        return SelectorPartType.ID_SELECTOR;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append('#').append(name);
    }

    @Override
    protected IdSelector makeCopy(Prefix prefix, SupportMatrix support) {
        return new IdSelector(name);
    }
}
