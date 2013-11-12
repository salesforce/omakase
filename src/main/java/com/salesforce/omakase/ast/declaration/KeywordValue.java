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

import com.google.common.base.Optional;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.parser.declaration.KeywordValueParser;
import com.salesforce.omakase.util.As;
import com.salesforce.omakase.util.Copy;
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
public final class KeywordValue extends AbstractTerm {
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

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(keyword);
    }

    @Override
    public KeywordValue copy() {
        // TESTME
        return Copy.comments(this, new KeywordValue(keyword));
    }

    @Override
    public KeywordValue copyWithPrefix(Prefix prefix, SupportMatrix support) {
        // TESTME
        if (this.isDetached()) return copy();

        Optional<Declaration> declaration = this.group().get().parent().parentDeclaration();
        if (!declaration.isPresent()) return copy();

        if (!declaration.get().isProperty(Property.TRANSITION)) return copy();

        Property property = Property.lookup(keyword);
        if (property == null) return copy();

        if (!support.requiresPrefixForProperty(prefix, property)) return copy();

        return Copy.comments(this, new KeywordValue(prefix + keyword));
    }

    @Override
    public String toString() {
        return As.string(this).add("keyword", keyword).addUnlessEmpty("comments", comments()).toString();
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
