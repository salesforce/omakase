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

import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.BroadcastRequirement;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.atrule.MediaQueryListParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * Represents a list of media queries.
 * <p>
 * In the following example the media query list is everything until the opening curly brace:
 * <pre>    {@code @}media all and (min-width: 800px), projection and (color) { ... }</pre>
 *
 * @author nmcwilliams
 * @see MediaQueryListParser
 */
@Subscribable
@Description(value = "full media query string", broadcasted = BroadcastRequirement.REFINED_AT_RULE)
public final class MediaQueryList extends AbstractAtRuleMember implements AtRuleExpression {
    private final SyntaxCollection<MediaQueryList, MediaQuery> queries;

    /**
     * Constructs a new {@link MediaQueryList} instance.
     * <p>
     * This should be used for dynamically created declarations.
     */
    public MediaQueryList() {
        this(-1, -1);
    }

    /**
     * Constructs a new {@link MediaQuery} instance.
     *  @param line
     *     The line number.
     * @param column
     *     The column number.
     */
    public MediaQueryList(int line, int column) {
        super(line, column);
        queries = new LinkedSyntaxCollection<>(this);
    }

    /**
     * Gets the {@link SyntaxCollection} of {@link MediaQuery} objects. You can used the {@link SyntaxCollection} to remove or add
     * additional queries.
     *
     * @return The media queries.
     */
    public SyntaxCollection<MediaQueryList, MediaQuery> queries() {
        return queries;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster, Status status) {
        if (status() == status) {
            queries.propagateBroadcast(broadcaster, status);
            super.propagateBroadcast(broadcaster, status);
        }
    }

    @Override
    public boolean isWritable() {
        return super.isWritable() && !queries.isEmptyOrNoneWritable();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        for (MediaQuery query : queries) {
            writer.writeInner(query, appendable);
            if (query.next().isPresent()) {
                appendable.append(',');
                appendable.spaceIf(!writer.isCompressed());
            }
        }
    }

    @Override
    public MediaQueryList copy() {
        MediaQueryList copy = new MediaQueryList().copiedFrom(this);
        for (MediaQuery query : queries) {
            copy.queries().append(query.copy());
        }
        return copy;
    }
}
