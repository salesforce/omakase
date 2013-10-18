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

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.atrule.MediaQueryParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkState;

/**
 * TESTME
 * <p/>
 * Represents a media query.
 * <p/>
 * In the following example:
 * <pre>    {@code @}media all and (min-width: 800px), projection and (color) { ... }</pre>
 * <p/>
 * There are two media queries,
 * <p/>
 * 1) {@code all and (min-width: 800px)}
 * <p/>
 * 2) {@code projection and (color)}
 *
 * @author nmcwilliams
 * @see MediaQueryParser
 */
public final class MediaQuery extends AbstractGroupable<MediaQueryList, MediaQuery> {
    private Optional<String> type = Optional.absent();
    private Optional<Restriction> restriction = Optional.absent();
    private final SyntaxCollection<MediaQuery, MediaQueryExpression> expressions;

    public enum Restriction {
        ONLY,
        NOT
    }

    public MediaQuery() {
        this(-1, -1, null);
    }

    public MediaQuery(int line, int column, Broadcaster broadcaster) {
        super(line, column);
        this.expressions = StandardSyntaxCollection.create(this, broadcaster);
    }

    public MediaQuery restriction(Restriction restriction) {
        this.restriction = Optional.fromNullable(restriction);
        checkState(!this.restriction.isPresent() || type.isPresent(), "cannot have a restriction without a media type");
        return this;
    }

    public Optional<Restriction> restriction() {
        return restriction;
    }

    public MediaQuery type(String type) {
        this.type = type != null ? Optional.of(type.toLowerCase()) : Optional.<String>absent();
        return this;
    }

    public Optional<String> type() {
        return type;
    }

    public SyntaxCollection<MediaQuery, MediaQueryExpression> expressions() {
        return expressions;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        super.propagateBroadcast(broadcaster);
        expressions().propagateBroadcast(broadcaster);
    }

    @Override
    protected MediaQuery self() {
        return this;
    }

    @Override
    public boolean isWritable() {
        return type.isPresent() || !expressions.isEmptyOrNoneWritable();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        // the restriction
        if (restriction.isPresent()) {
            appendable.append(restriction.get() == Restriction.ONLY ? "only" : "and");
            appendable.space();
        }

        // the feature (if feature == all then it can be left out)
        boolean printedType = false;
        if (type.isPresent() && !type.get().equals("all")) {
            printedType = true;
            appendable.append(type.get());
        }

        boolean isFirst = true;
        for (MediaQueryExpression expression : expressions) {
            if (!isFirst || printedType) {
                appendable.append(" and");
                writer.write(expression, appendable);
            }
            isFirst = false;
        }
    }
}
