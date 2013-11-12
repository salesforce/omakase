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
import com.salesforce.omakase.util.As;
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
    private Optional<MediaRestriction> restriction = Optional.absent();
    private final SyntaxCollection<MediaQuery, MediaQueryExpression> expressions;

    /**
     * Constructs a new {@link MediaQuery} instance.
     * <p/>
     * This should be used for dynamically created declarations.
     */
    public MediaQuery() {
        this(-1, -1, null);
    }

    /**
     * Constructs a new {@link MediaQuery} instance.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param broadcaster
     *     Used for broadcasting.
     */
    public MediaQuery(int line, int column, Broadcaster broadcaster) {
        super(line, column);
        this.expressions = StandardSyntaxCollection.create(this, broadcaster);
    }

    /**
     * Sets the media restriction (only|not). The type must be present if the given value is not null.
     *
     * @param restriction
     *     The restriction, or null to remove it.
     *
     * @return this, for chaining.
     *
     * @throws IllegalStateException
     *     When setting the restriction to null but the type is still present.
     */
    public MediaQuery restriction(MediaRestriction restriction) {
        this.restriction = Optional.fromNullable(restriction);
        checkState(!this.restriction.isPresent() || type.isPresent(), "cannot have a restriction without a media type");
        return this;
    }

    /**
     * Gets the media restriction (only|not).
     *
     * @return The media restriction, or {@link Optional#absent()} if not present.
     */
    public Optional<MediaRestriction> restriction() {
        return restriction;
    }

    /**
     * Sets the media type (e.g., "screen" or "all"). Note that the type will be automatically lower-cased.
     *
     * @param type
     *     The media type, or null to remove it.
     *
     * @return this, for chaining.
     */
    public MediaQuery type(String type) {
        this.type = type != null ? Optional.of(type.toLowerCase()) : Optional.<String>absent();
        return this;
    }

    /**
     * Gets the media type, if present.
     *
     * @return The media type, or {@link Optional#absent()} if not present.
     */
    public Optional<String> type() {
        return type;
    }

    /**
     * Gets the collection of expressions. You can add, remove or change the expression using the methods on the returned {@link
     * SyntaxCollection} object.
     *
     * @return The expressions.
     */
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
            writer.writeInner(restriction.get(), appendable);
            appendable.space();
        }

        // the type (if type == all then it can be left out)
        boolean printedType = false;
        if (restriction.isPresent() || (type.isPresent() && !type.get().equals("all"))) {
            printedType = true;
            appendable.append(type.get());
        }

        // the expressions
        boolean isFirst = true;
        for (MediaQueryExpression expression : expressions) {
            if ((!isFirst || printedType) && expression.isWritable()) {
                appendable.append(" and ");
            }
            writer.writeInner(expression, appendable);
            isFirst = false;
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("restriction", restriction)
            .add("type", type)
            .add("expressions", expressions)
            .toString();
    }
}
