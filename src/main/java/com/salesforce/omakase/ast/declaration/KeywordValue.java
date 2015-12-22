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
import com.salesforce.omakase.ast.Named;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.parser.declaration.KeywordValueParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * A keyword value (e.g., inline-block).
 *
 * @author nmcwilliams
 * @see KeywordValueParser
 */
@Subscribable
@Description(value = "individual keyword value", broadcasted = REFINED_DECLARATION)
public final class KeywordValue extends AbstractTerm implements Named {
    private String keyword;

    /**
     * Constructs a new {@link KeywordValue} instance.
     * <p/>
     * If dynamically creating a new instance then use {@link #KeywordValue(Keyword)} or {@link #KeywordValue(String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param keyword
     *     The keyword.
     */
    public KeywordValue(int line, int column, String keyword) {
        super(line, column);
        this.keyword = keyword;
    }

    /**
     * Constructs a new {@link KeywordValue} instance (used for dynamically created {@link Syntax} units). Prefer {@link
     * #KeywordValue(Keyword)} over this.
     *
     * @param keyword
     *     The keyword.
     */
    public KeywordValue(String keyword) {
        keyword(keyword);
    }

    /**
     * Constructs a new {@link KeywordValue} instance with the given {@link Keyword} (used for dynamically created {@link Syntax}
     * units).
     *
     * @param keyword
     *     The keyword.
     */
    public KeywordValue(Keyword keyword) {
        keyword(keyword);
    }

    /**
     * Sets the keyword value. Prefer {@link #keyword(Keyword)} over this one.
     *
     * @param keyword
     *     The keyword.
     *
     * @return this, for chaining.
     */
    public KeywordValue keyword(String keyword) {
        this.keyword = checkNotNull(keyword, "keyword cannot be null");
        return this;
    }

    /**
     * Sets the keyword value.
     *
     * @param keyword
     *     The keyword.
     *
     * @return this, for chaining.
     */
    public KeywordValue keyword(Keyword keyword) {
        return keyword(keyword.toString());
    }

    /**
     * Gets the keyword value.
     *
     * @return The keyword.
     */
    public String keyword() {
        return keyword;
    }

    /**
     * Gets the exact matching {@link Keyword} instance, if one exists (it may not exist if this is an unknown keyword).
     *
     * @return The {@link Keyword}.
     */
    public Optional<Keyword> asKeyword() {
        return Optional.fromNullable(Keyword.lookup(keyword));
    }

    @Override
    public String name() {
        return keyword();
    }

    /**
     * Gets the keyword value. Prefer to use {@link #keyword()}, which is identical to this method.
     *
     * @return The keyword.
     */
    @Override
    public String textualValue() {
        return keyword();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(keyword);
    }

    @Override
    public KeywordValue copy() {
        return new KeywordValue(keyword).copiedFrom(this);
    }

    /**
     * Creates a new {@link KeywordValue} instance from the given keyword string. Prefer to use {@link #of(Keyword)} over this.
     * <p/>
     * Example:
     * <pre>
     * <code>KeywordValue.of("left");</code>
     * </pre>
     *
     * @param keyword
     *     The keyword.
     *
     * @return The new {@link KeywordValue} instance.
     */
    public static KeywordValue of(String keyword) {
        return new KeywordValue(keyword);
    }

    /**
     * Creates a new {@link KeywordValue} instance from the given {@link Keyword}.
     * <p/>
     * Example:
     * <pre>
     * <code>KeywordValue.of(Keyword.LEFT);</code>
     * </pre>
     *
     * @param keyword
     *     The keyword.
     *
     * @return The new {@link KeywordValue} instance.
     */
    public static KeywordValue of(Keyword keyword) {
        return new KeywordValue(keyword);
    }
}
