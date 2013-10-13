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

package com.salesforce.omakase.ast.declaration.value;

import com.google.common.base.Optional;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a url function value.
 *
 * @author nmcwilliams
 */
public class UrlFunctionValue extends AbstractSyntax implements RefinedFunctionValue {
    private Optional<QuotationMode> quotationMode = Optional.absent();
    private String url;

    /**
     * Creates a new {@link UrlFunctionValue} instance. If the url is quoted then use {@link #quotationMode(QuotationMode)} to
     * specify the type of quotes to use.
     * <p/>
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
     * <p/>
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
     * <p/>
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
        this.quotationMode = Optional.fromNullable(quotationMode);
        return this;
    }

    /**
     * Gets the quotation mode, if present.
     *
     * @return The quotation mode, or {@link Optional#absent()} if not present.
     */
    public Optional<QuotationMode> quotationMode() {
        return quotationMode;
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append("url(");
        if (quotationMode.isPresent()) {
            if (quotationMode.get() == QuotationMode.DOUBLE) {
                appendable.append('"');
            } else {
                appendable.append('\'');
            }
        }

        appendable.append(url);

        if (quotationMode.isPresent()) {
            if (quotationMode.get() == QuotationMode.DOUBLE) {
                appendable.append('"');
            } else {
                appendable.append('\'');
            }
        }

        appendable.append(')');
    }

    @Override
    public String toString() {
        return As.string(this)
            .add("url", url)
            .addIf(quotationMode.isPresent(), "quotes", quotationMode)
            .toString();
    }
}
