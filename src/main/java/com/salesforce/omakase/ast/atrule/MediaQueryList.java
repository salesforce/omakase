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

import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
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
 * TESTME
 * <p/>
 * Represents a list of media queries.
 * <p/>
 * In the following example the media query list is everything until the opening curly brace:
 * <pre>    {@code@}media all and (min-width: 800px), projection and (color) { ... }</pre>
 *
 * @author nmcwilliams
 * @see MediaQueryListParser
 */
@Subscribable
@Description(value = "full media query string", broadcasted = BroadcastRequirement.REFINED_AT_RULE)
public final class MediaQueryList extends AbstractSyntax implements AtRuleExpression {
    private final SyntaxCollection<MediaQueryList, MediaQuery> queries;

    public MediaQueryList() {
        this(-1, -1, null);
    }

    public MediaQueryList(int line, int column, Broadcaster broadcaster) {
        super(line, column);
        queries = StandardSyntaxCollection.create(this, broadcaster);
    }

    public SyntaxCollection<MediaQueryList, MediaQuery> queries() {
        return queries;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        super.propagateBroadcast(broadcaster);
        queries.propagateBroadcast(broadcaster);
    }

    @Override
    public boolean isWritable() {
        return !queries.isEmptyOrNoneWritable();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        for (MediaQuery query : queries) {
            writer.write(query, appendable);
            if (!query.haxNextAndNextNotDetached()) {
                appendable.append(',');
                appendable.spaceIf(!writer.isCompressed());
            }
        }
    }
}