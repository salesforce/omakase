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

import com.google.common.collect.Sets;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.selector.PseudoSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_SELECTOR;

/**
 * Represents a CSS pseudo element selector.
 *
 * @author nmcwilliams
 * @see PseudoSelectorParser
 */
@Subscribable
@Description(value = "pseudo element selector segment", broadcasted = REFINED_SELECTOR)
public final class PseudoElementSelector extends AbstractSelectorPart implements SimpleSelector {
    /** these can use pseudo class syntax but are actually pseudo elements */
    public static final Set<String> POSERS = Sets.newHashSet("first-line", "first-letter", "before", "after");

    private String name;

    /**
     * Constructs a new {@link PseudoElementSelector} selector with the given name.
     * <p/>
     * If dynamically creating a new instance then use {@link #PseudoElementSelector(String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param name
     *     Name of the pseudo element.
     */
    public PseudoElementSelector(int line, int column, String name) {
        super(line, column);
        this.name = name.toLowerCase();
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param name
     *     Name of the pseudo element.
     */
    public PseudoElementSelector(String name) {
        name(name);
    }

    /**
     * Sets the name of the selector.
     *
     * @param name
     *     The new name.
     *
     * @return this, for chaining.
     */
    public PseudoElementSelector name(String name) {
        checkNotNull(name, "name cannot be null");
        this.name = name.toLowerCase();
        return this;
    }

    /**
     * Gets the selector name (e.g., "before").
     *
     * @return The selector name.
     */
    public String name() {
        return name;
    }

    @Override
    public SelectorPartType type() {
        return SelectorPartType.PSEUDO_ELEMENT_SELECTOR;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(POSERS.contains(name) ? ":" : "::").append(name);
    }

    @Override
    protected SelectorPart makeCopy(Prefix prefix, SupportMatrix support) {
        // TESTME
        return new PseudoElementSelector(name);
    }
}
