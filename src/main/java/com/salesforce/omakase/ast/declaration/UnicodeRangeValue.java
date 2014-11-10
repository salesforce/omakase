/*
 * Copyright (C) 2014 salesforce.com, inc.
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

package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.declaration.UnicodeRangeValueParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * A unicode-range value (e.g., from within a @font-face rule).
 * <p/>
 * There are three types of allowed values:
 * <p/>
 * - single codepoint (e.g. {@code U+416})
 * <p/>
 * - interval range (e.g. {@code U+400-4ff})
 * <p/>
 * - wildcard range (e.g. {@code U+4??})
 *
 * @author nmcwilliams
 * @see UnicodeRangeValueParser
 */
@Subscribable
@Description(value = "unicode range value", broadcasted = REFINED_DECLARATION)
public class UnicodeRangeValue extends AbstractTerm {
    private String value;

    /**
     * Constructs a new instance of a {@link UnicodeRangeValue}.
     * <p/>
     * If dynamically creating a new instance then use {@link #UnicodeRangeValue(String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param value
     *     The unicode range value. Automatically lower-cased.
     */
    public UnicodeRangeValue(int line, int column, String value) {
        super(line, column);
        value(value);
    }

    /**
     * Constructs a new instance of a {@link UnicodeRangeValue} (used for dynamically created {@link Syntax} units).
     *
     * @param value
     *     The unicode range value. Automatically lower-cased.
     */
    public UnicodeRangeValue(String value) {
        value(value);
    }

    /**
     * Constructs a new instance of a {@link UnicodeRangeValue} (used for dynamically created {@link Syntax} units). Only use this
     * when you already know the string is lower-cased.
     *
     * @param value
     *     The unicode range value. Automatically lower-cased.
     * @param lowerCase
     *     Specifies whether the string is already lower-cased.
     */
    public UnicodeRangeValue(String value, boolean lowerCase) {
        if (lowerCase) {
            this.value = value;
        } else {
            value(value);
        }
    }

    /**
     * Sets the unicode-range value (will be converted to lower-case).
     *
     * @param value
     *     The unicode-range value. Automatically lower-cased.
     *
     * @return this, for chaining.
     */
    public UnicodeRangeValue value(String value) {
        checkNotNull(value, "value cannot be null");
        this.value = value.toLowerCase();
        return this;
    }

    /**
     * Gets the unicode-range value.
     *
     * @return The unicode-range value.
     */
    public String value() {
        return value;
    }

    /**
     * Gets the unicode-range value. Prefer to use {@link #value()}, which is identical to this method.
     *
     * @return The unicode-range value.
     */
    @Override
    public String textualValue() {
        return value();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(value);
    }

    @Override
    protected PropertyValueMember makeCopy(Prefix prefix, SupportMatrix support) {
        return new UnicodeRangeValue(value, true);
    }
}
