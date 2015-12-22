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

package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
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
    public UnicodeRangeValue copy() {
        return new UnicodeRangeValue(value, true).copiedFrom(this);
    }
}
