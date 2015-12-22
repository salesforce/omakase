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

package com.salesforce.omakase.ast.selector;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.selector.AttributeSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_SELECTOR;

/**
 * Represents a CSS attribute selector.
 *
 * @author nmcwilliams
 * @see AttributeSelectorParser
 */
@Subscribable
@Description(value = "attribute selector segment", broadcasted = REFINED_SELECTOR)
public final class AttributeSelector extends AbstractSelectorPart implements SimpleSelector {
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
    public SelectorPartType type() {
        return SelectorPartType.ATTRIBUTE_SELECTOR;
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
            writer.writeInner(matchType.get(), appendable);

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
    public AttributeSelector copy() {
        AttributeSelector copy = new AttributeSelector(attribute).copiedFrom(this);
        if (matchType.isPresent()) {
            copy.match(matchType.get(), value.get());
        }
        return copy;
    }
}
