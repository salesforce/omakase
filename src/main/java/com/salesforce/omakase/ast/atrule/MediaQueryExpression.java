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

package com.salesforce.omakase.ast.atrule;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.declaration.PropertyValueMember;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * Represents a media query expression.
 * <p>
 * In the following example:
 * <pre>    {@code @}media all and (min-width: 800px) { ... }</pre>
 *
 * The expression is <code>(min-width: 800px)</code>
 *
 * @author nmcwilliams
 * @see MediaQueryExpressionParser
 */
public final class MediaQueryExpression extends AbstractGroupable<MediaQuery, MediaQueryExpression> {
    private List<PropertyValueMember> terms;
    private String feature;

    /**
     * Creates a new {@link MediaQueryExpression} instance.
     * <p>
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
    public MediaQueryExpression terms(Iterable<PropertyValueMember> terms) {
        this.terms = Lists.newArrayList(checkNotNull(terms, "terms cannot be null"));
        return this;
    }

    /**
     * Gets the list of terms and operators.
     *
     * @return The list of  terms and operators.
     */
    public List<PropertyValueMember> terms() {
        return terms != null ? terms : new ArrayList<>();
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

            for (PropertyValueMember term : terms) {
                writer.writeInner(term, appendable);
            }
        }

        // closing parent
        appendable.append(')');
    }

    @Override
    public MediaQueryExpression copy() {
        MediaQueryExpression copy = new MediaQueryExpression(feature).copiedFrom(this);

        if (terms != null && !terms.isEmpty()) {
            List<PropertyValueMember> membersCopy = new ArrayList<>();
            for (PropertyValueMember member : terms) {
                membersCopy.add(member.copy());
            }

            copy.terms(membersCopy);
        }

        return copy;
    }
}
