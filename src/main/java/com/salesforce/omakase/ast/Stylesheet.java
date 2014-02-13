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

package com.salesforce.omakase.ast;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.raw.StylesheetParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.Iterator;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.AUTOMATIC;

/**
 * The root-level {@link Syntax} object.
 * <p/>
 * Comments that appear in the original CSS source at the beginning of the stylesheet are actually going to be associated with the
 * first {@link Statement} in the sheet instead. Comments after the last {@link Statement} (or if the sheet is empty) will be
 * placed in the {@link #orphanedComments()} list.
 *
 * @author nmcwilliams
 * @see StylesheetParser
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public final class Stylesheet extends AbstractSyntax<StatementIterable> implements StatementIterable {
    private final SyntaxCollection<StatementIterable, Statement> statements;
    private final transient Broadcaster broadcaster;

    /**
     * Constructs a new {@link Stylesheet} instance.
     *
     * @param broadcaster
     *     Used to broadcast new units.
     */
    public Stylesheet(Broadcaster broadcaster) {
        super(1, 1);
        statements = new LinkedSyntaxCollection<StatementIterable, Statement>(this, broadcaster);
        this.broadcaster = broadcaster;
    }

    /**
     * Creates a new {@link Stylesheet} <em>with no {@link Broadcaster}</em>. This is only appropriate for dynamically created
     * stylesheets (no plugins will run).
     */
    public Stylesheet() {
        this(null);
    }

    @Override
    public SyntaxCollection<StatementIterable, Statement> statements() {
        return statements;
    }

    /**
     * Appends a new {@link Statement} to the end of this stylesheet.
     *
     * @param statement
     *     The {@link Statement} to append.
     *
     * @return this, for chaining.
     */
    public Stylesheet append(Statement statement) {
        statements.append(statement);
        return this;
    }

    @Override
    public Iterator<Statement> iterator() {
        return statements.iterator();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        for (Statement statement : statements) {
            writer.writeInner(statement, appendable);
        }
    }

    @Override
    protected Stylesheet makeCopy(Prefix prefix, SupportMatrix support) {
        Stylesheet copy = new Stylesheet(broadcaster);
        for (Statement statement : statements) {
            copy.append(statement.copy());
        }
        return copy;
    }
}
