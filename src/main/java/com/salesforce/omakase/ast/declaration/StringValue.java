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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

import java.io.IOException;

import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * A string value, e.g., "Times New Roman".
 * <p>
 * Note that the reason a setter for quotation mode doesn't exist is that it must not be set independent of the content (which
 * could contain incompatible), so use {@link #content(QuotationMode, String)} (and verify the content and quotation mode are
 * compatible) instead.
 *
 * @author nmcwilliams
 * @see StringValueParser
 */
@Subscribable
@Description(value = "individual string value", broadcasted = REFINED_DECLARATION)
public final class StringValue extends AbstractTerm {
    private QuotationMode mode;
    private String content;

    /**
     * Constructs a new {@link StringValue} instance.
     * <p>
     * The {@link QuotationMode} is required so that we can preserve the original quotes used in the source. Performance-wise,
     * there is no reason to change it from the original, and also it keeps us from having to mess around with escaping.
     * <p>
     * * If dynamically creating a new instance then use {@link #StringValue(QuotationMode, String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param mode
     *     The {@link QuotationMode} to use when printing out the value.
     * @param content
     *     The content of the string.
     */
    public StringValue(int line, int column, QuotationMode mode, String content) {
        super(line, column);
        this.mode = mode;
        this.content = content;
    }

    /**
     * Constructs a new {@link StringValue} instance (used for dynamically created {@link Syntax} units).
     *
     * @param mode
     *     The {@link QuotationMode} to use when printing out the value.
     * @param content
     *     The content of the string.
     */
    public StringValue(QuotationMode mode, String content) {
        content(mode, content);
    }

    /**
     * Sets the content of the string.
     *
     * @param mode
     *     The {@link QuotationMode} to use when printing out the value.
     * @param content
     *     The content.
     *
     * @return this, for chaining.
     */
    public StringValue content(QuotationMode mode, String content) {
        this.mode = checkNotNull(mode, "mode cannot be null");
        this.content = checkNotNull(content, "content cannot be null");
        return this;
    }

    /**
     * Gets the content of the string.
     *
     * @return The content of the string.
     */
    public String content() {
        return content;
    }

    /**
     * Gets the {@link QuotationMode}.
     *
     * @return The {@link QuotationMode}.
     */
    public QuotationMode mode() {
        return mode;
    }

    /**
     * Gets the content of the string. Prefer to use {@link #content()}, which is identical to this method.
     *
     * @return The content of the string.
     */
    @Override
    public String textualValue() {
        return content();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (mode == QuotationMode.SINGLE) {
            appendable.append('\'').append(content).append('\'');
        } else {
            appendable.append('"').append(content).append('"');
        }
    }

    @Override
    public StringValue copy() {
        return new StringValue(mode, content).copiedFrom(this);
    }

    /**
     * Creates a new {@link StringValue} instance using the given {@link QuotationMode} and content.
     * <p>
     * Example:
     * <pre>
     * <code>StringValue.of("Times new Roman")</code>
     * </pre>
     *
     * @param mode
     *     The {@link QuotationMode} to use when printing out the value.
     * @param content
     *     The content of the string.
     *
     * @return The new {@link StringValue} instance.
     */
    public static StringValue of(QuotationMode mode, String content) {
        return new StringValue(mode, content);
    }
}
