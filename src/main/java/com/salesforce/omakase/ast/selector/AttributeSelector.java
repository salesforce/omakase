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

import com.google.common.base.Optional;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.selector.AttributeSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_SELECTOR;

/**
 * Represents a CSS attribute selector.
 *
 * @author nmcwilliams
 * @see AttributeSelectorParser
 */
@Subscribable
@Description(value = "attribute selector segment", broadcasted = REFINED_SELECTOR)
public class AttributeSelector extends AbstractSelectorPart implements SimpleSelector {
    private static final Pattern SIMPLE_VALUE = Pattern.compile("[a-zA-Z][a-zA-Z0-9-_]*");

    private String attribute;
    private Optional<AttributeMatchType> matchType = Optional.absent();
    private Optional<String> value = Optional.absent();

    /**
     * Creates a new instance with the given line and column numbers.
     * <p/>
     * If dynamically creating a new instance then use {@link #AttributeSelector(String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param attribute
     *     The the attribute.
     */
    public AttributeSelector(int line, int column, String attribute) {
        super(line, column);
        this.attribute = attribute;
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param attribute
     *     The attribute.
     */
    public AttributeSelector(String attribute) {
        attribute(attribute);
    }

    /**
     * Sets the attribute value.
     *
     * @param attribute
     *     The attribute.
     *
     * @return this, for chaining.
     */
    public AttributeSelector attribute(String attribute) {
        this.attribute = checkNotNull(attribute, "attribute cannot be null");
        return this;
    }

    /**
     * Gets the attribute value.
     *
     * @return The attribute value.
     */
    public String attribute() {
        return attribute;
    }

    /**
     * Specifies the match type and expected match value. If you want to <em>remove</em> the type and value then use {@link
     * #matchAll()} instead.
     *
     * @param matchType
     *     The match type.
     * @param value
     *     The match value.
     *
     * @return this, for chaining.
     */
    public AttributeSelector match(AttributeMatchType matchType, String value) {
        checkNotNull(matchType, "matchType cannot be null");
        checkNotNull(value, "value cannot be null");

        this.matchType = Optional.of(matchType);
        this.value = Optional.of(value);

        return this;
    }

    /**
     * Removes the match type and value, if present. This essentially makes the attribute selector match anything with the
     * attribute, e.g, {@code [href='blah']} becomes {@code [href]}.
     *
     * @return this, for chaining.
     */
    public AttributeSelector matchAll() {
        matchType = Optional.absent();
        value = Optional.absent();
        return this;
    }

    /**
     * Gets the {@link AttributeMatchType} (e.g., "=", "^=", "~=", etc...), if present.
     *
     * @return The {@link AttributeMatchType}, or {@link Optional#absent()} if not specified.
     */
    public Optional<AttributeMatchType> matchType() {
        return matchType;
    }

    /**
     * Gets the match value, if present.
     *
     * @return The match value, or {@link Optional#absent()} if not specified.
     */
    public Optional<String> value() {
        return value;
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
        return SelectorPartType.ATTRIBUTE_SELECTOR;
    }

    @Override
    protected SelectorPart self() {
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        // opening bracket
        appendable.append('[');

        // attribute name
        appendable.append(attribute);

        //  the match and value if present
        if (matchType.isPresent()) {
            // match type
            writer.write(matchType.get(), appendable);

            // the value. In simple cases where we know quotes aren't needed we omit them.
            // This could be handled better...
            final String val = value.get();
            if (SIMPLE_VALUE.matcher(val).matches()) {
                appendable.append(val);
            } else {
                appendable.append('"').append(val).append('"');
            }
        }

        // closing bracket
        appendable.append(']');
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("abstract", super.toString())
            .add("attribute", attribute)
            .add("matchType", matchType)
            .add("value", value)
            .toString();
    }
}
