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

package com.salesforce.omakase.ast.atrule;

import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.declaration.TermListMember;
import com.salesforce.omakase.parser.atrule.MediaQueryExpressionParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a media query expression.
 * <p/>
 * In the following example:
 * <pre>    {@code @}media all and (min-width: 800px) { ... }</pre>
 * <p/>
 * The expression is <code>(min-width: 800px)</code>
 *
 * @author nmcwilliams
 * @see MediaQueryExpressionParser
 */
public final class MediaQueryExpression extends AbstractGroupable<MediaQuery, MediaQueryExpression> {
    private List<TermListMember> terms;
    private String feature;

    /**
     * Creates a new {@link MediaQueryExpression} instance.
     * <p/>
     * This should be used for dynamically created declarations.
     *
     * @param feature
     *     The media feature name, e.g., "min-width".
     */
    public MediaQueryExpression(String feature) {
        this(-1, -1, feature);
    }

    /**
     * Creates a new {@link MediaQueryExpression} instance.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param feature
     *     The media feature name, e.g., "min-width".
     */
    public MediaQueryExpression(int line, int column, String feature) {
        super(line, column);
        feature(feature);
    }

    /**
     * Sets the terms and operators in this {@link MediaQueryExpression}.
     *
     * @param terms
     *     The list of terms and operators.
     *
     * @return this, for chaining.
     */
    public MediaQueryExpression terms(Iterable<TermListMember> terms) {
        this.terms = Lists.newArrayList(checkNotNull(terms, "terms cannot be null"));
        return this;
    }

    /**
     * Gets the list of terms and operators.
     *
     * @return The list of  terms and operators.
     */
    public List<TermListMember> terms() {
        return terms != null ? terms : Lists.<TermListMember>newArrayList();
    }

    /**
     * Sets the media feature name.
     *
     * @param feature
     *     The media feature, e.g., "min-width".
     *
     * @return this, for chaining.
     */
    public MediaQueryExpression feature(String feature) {
        this.feature = checkNotNull(feature, "feature cannot be null");
        return this;
    }

    /**
     * Gets the media feature name.
     *
     * @return The media feature name.
     */
    public String feature() {
        return feature;
    }

    @Override
    protected MediaQueryExpression self() {
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        // opening paren
        appendable.append('(');

        // the feature
        appendable.append(feature);

        // the terms
        if (terms != null && !terms.isEmpty()) {
            appendable.append(':');
            appendable.spaceIf(!writer.isCompressed());

            for (TermListMember term : terms) {
                writer.write(term, appendable);
            }
        }

        // closing parent
        appendable.append(')');
    }

    @Override
    public String toString() {
        return As.string(this).add("feature", feature).add("terms", terms).toString();
    }
}
