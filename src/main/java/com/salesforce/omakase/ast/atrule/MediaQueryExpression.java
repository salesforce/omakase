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
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.declaration.TermListMember;
import com.salesforce.omakase.parser.atrule.MediaQueryExpressionParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TESTME
 * <p/>
 * Represents a media query expression.
 * <p/>
 * In the following example:
 * <pre>    {@code@}media all and (min-width: 800px) { ... }</pre>
 * <p/>
 * The expression is {@code (min-width: 800px)}
 *
 * @author nmcwilliams
 * @see MediaQueryExpressionParser
 */
public final class MediaQueryExpression extends AbstractGroupable<MediaQuery, MediaQueryExpression> {
    private String feature;
    private List<TermListMember> terms;

    public MediaQueryExpression(String feature) {
        this(-1, -1, feature);
    }

    public MediaQueryExpression(int line, int column, String feature) {
        super(line, column);
        feature(feature);
    }

    public MediaQueryExpression terms(Iterable<TermListMember> terms) {
        this.terms = Lists.newArrayList(checkNotNull(terms, "terms cannot be null"));
        return this;
    }

    public List<TermListMember> terms() {
        return terms != null ? terms : Lists.<TermListMember>newArrayList();
    }

    private MediaQueryExpression feature(String feature) {
        this.feature = checkNotNull(feature, "feature cannot be null");
        return this;
    }

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
        if (!terms.isEmpty()) {
            appendable.append(':');
            appendable.spaceIf(!writer.isCompressed());

            for (TermListMember term : terms) {
                writer.write(term, appendable);
            }
        }

        // closing parent
        appendable.append(')');
    }
}
