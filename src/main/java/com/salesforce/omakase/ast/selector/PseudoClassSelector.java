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
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.selector.PseudoSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.*;
import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_SELECTOR;

/**
 * TESTME
 * <p/>
 * Represents a CSS pseudo class selector.
 * <p/>
 * Note that even though some pseudo elements can be written using the pseudo class format, they are <b>not</b> considered pseudo
 * classes in this library, but as {@link PseudoElementSelector}s.
 *
 * @author nmcwilliams
 * @see PseudoSelectorParser
 */
@Subscribable
@Description(value = "pseudo class selector segment", broadcasted = REFINED_SELECTOR)
public class PseudoClassSelector extends AbstractGroupable<SelectorPart> implements SimpleSelector {
    private String name;

    /**
     * Constructs a new {@link PseudoClassSelector} instance with the given name.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param name
     *     Name of the pseudo class.
     */
    public PseudoClassSelector(int line, int column, String name) {
        super(line, column);
        this.name = name;
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param name
     *     Name of the pseudo class.
     */
    public PseudoClassSelector(String name) {
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
    public PseudoClassSelector name(String name) {
        checkArgument(!PseudoElementSelector.POSERS.contains(name),
            String.format("%s must be created as a PseudoElementSelector", name));

        this.name = checkNotNull(name, "name cannot be null");
        return this;
    }

    /**
     * Gets the selector name (e.g., "hover").
     *
     * @return The selector name.
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
        return SelectorPartType.PSEUDO_CLASS_SELECTOR;
    }

    @Override
    protected PseudoClassSelector self() {
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (isDetached()) return;
        // TODO function args
        appendable.append(':').append(name);
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("position", super.toString())
            .add("name", name)
            .toString();
    }
}
