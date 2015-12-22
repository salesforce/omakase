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

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
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
        this.expressions = new LinkedSyntaxCollection<>(this, broadcaster);
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
        expressions().propagateBroadcast(broadcaster);
        super.propagateBroadcast(broadcaster);
    }

    @Override
    protected MediaQuery self() {
        return this;
    }

    @Override
    public boolean isWritable() {
        return super.isWritable() && (type.isPresent() || !expressions.isEmptyOrNoneWritable());
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
    public MediaQuery copy() {
        MediaQuery copy = new MediaQuery().copiedFrom(this);

        if (type.isPresent()) {
            copy.type(type.get());
        }

        if (restriction.isPresent()) {
            copy.restriction(restriction.get());
        }

        for (MediaQueryExpression expression : expressions) {
            copy.expressions().append(expression.copy());
        }

        return copy;
    }
}
