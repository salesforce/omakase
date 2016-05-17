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

import com.google.common.base.Optional;
import com.salesforce.omakase.broadcast.BroadcastRequirement;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a url function value.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "url function", broadcasted = BroadcastRequirement.REFINED_DECLARATION)
public final class UrlFunctionValue extends AbstractTerm implements FunctionValue {
    private QuotationMode quotationMode;
    private String url;

    /**
     * Creates a new {@link UrlFunctionValue} instance. If the url is quoted then use {@link #quotationMode(QuotationMode)} to
     * specify the type of quotes to use.
     * <p>
     * <b>Important:</b> The url should not be wrapped in quotes. Use {@link #quotationMode(QuotationMode)} for that.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param url
     *     The url. This should not be wrapped in quotes.
     */
    public UrlFunctionValue(int line, int column, String url) {
        super(line, column);
        this.url = url;
    }

    /**
     * Creates a new {@link UrlFunctionValue} instance. If the url is quoted then use {@link #quotationMode(QuotationMode)} to
     * specify the type of quotes to use.
     * <p>
     * <b>Important:</b> The url should not be wrapped in quotes. Use {@link #quotationMode(QuotationMode)} for that.
     *
     * @param url
     *     The url. This should not be wrapped in quotes.
     */
    public UrlFunctionValue(String url) {
        url(url);
    }

    /**
     * Sets the url (do not include quotes).
     * <p>
     * <b>Important:</b> The url should not be wrapped in quotes. Use {@link #quotationMode(QuotationMode)} for that.
     *
     * @param url
     *     The url.
     *
     * @return this, for chaining.
     */
    public UrlFunctionValue url(String url) {
        this.url = checkNotNull(url, "url cannot be null");
        return this;
    }

    /**
     * Gets the url (does not includes quotes).
     *
     * @return The url.
     */
    public String url() {
        return url;
    }

    /**
     * Sets the type of quotes to wrap around the url. Use null to remove and not output any quotes.
     *
     * @param quotationMode
     *     The type of quotes, or null for no quotes.
     *
     * @return this, for chaining.
     */
    public UrlFunctionValue quotationMode(QuotationMode quotationMode) {
        this.quotationMode = quotationMode;
        return this;
    }

    /**
     * Gets the quotation mode, if present.
     *
     * @return The quotation mode, or {@link Optional#absent()} if not present.
     */
    public Optional<QuotationMode> quotationMode() {
        return Optional.fromNullable(quotationMode);
    }

    /**
     * Gets the url (does not includes quotes). Prefer to use {@link #url()}, which is identical to this method.
     *
     * @return The url.
     */
    @Override
    public String textualValue() {
        return url();
    }

    @Override
    public String name() {
        return "url";
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append("url(");
        if (quotationMode != null) {
            appendable.append(quotationMode == QuotationMode.DOUBLE ? '"' : '\'');
        }

        appendable.append(url);

        if (quotationMode != null) {
            appendable.append(quotationMode == QuotationMode.DOUBLE ? '"' : '\'');
        }

        appendable.append(')');
    }

    @Override
    public UrlFunctionValue copy() {
        return new UrlFunctionValue(url).quotationMode(quotationMode).copiedFrom(this);
    }
}
