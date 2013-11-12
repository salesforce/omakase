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

package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.declaration.StringValueParser;
import com.salesforce.omakase.util.As;
import com.salesforce.omakase.util.Copy;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * A string value, e.g., "Times New Roman".
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
     * <p/>
     * The {@link QuotationMode} is required so that we can preserve the original quotes used in the source. Performance-wise,
     * there is no reason to change it from the original, and also it keeps us from having to mess around with escaping.
     * <p/>
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
        // TESTME
        return Copy.comments(this, new StringValue(mode, content));
    }

    @Override
    public String toString() {
        return As.string(this).add("content", content).addUnlessEmpty("comments", comments()).toString();
    }

    /**
     * Creates a new {@link StringValue} instance using the given {@link QuotationMode} and content.
     * <p/>
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
