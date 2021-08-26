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

package com.salesforce.omakase.ast;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.AUTOMATIC;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * The root-level {@link Syntax} object.
 * <p>
 * Comments that appear in the original CSS source at the beginning of the stylesheet are actually going to be associated with the
 * first {@link Statement} in the sheet instead. Comments after the last {@link Statement} (or if the sheet is empty) will be
 * placed in the {@link #orphanedComments()} list.
 *
 * @author nmcwilliams
 * @see StylesheetParser
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public final class Stylesheet extends AbstractSyntax implements StatementIterable {
    private final SyntaxCollection<StatementIterable, Statement> statements;

    /**
     * Constructs a new {@link Stylesheet} instance.
     */
    public Stylesheet() {
        super(1, 1);
        statements = new LinkedSyntaxCollection<>(this);
    }

    @Override
    public SyntaxCollection<StatementIterable, Statement> statements() {
        return statements;
    }

    /**
     * Returns just the {@link Rule}s within this {@link Stylesheet} (does not include at-rules or rules within at-rules).
     *
     * @return The list of rules.
     */
    public List<Rule> rules() {
        return statements.stream().filter(Rule.class::isInstance).map(Rule.class::cast).collect(Collectors.toList());
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
    public void propagateBroadcast(Broadcaster broadcaster, Status status) {
        if (status() == status) {
            statements.propagateBroadcast(broadcaster, status);
            super.propagateBroadcast(broadcaster, status);
        }
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        for (Statement statement : statements) {
            writer.writeInner(statement, appendable);
        }
    }

    @Override
    public Stylesheet copy() {
        Stylesheet copy = new Stylesheet().copiedFrom(this);
        for (Statement statement : statements) {
            copy.append(statement.copy());
        }
        return copy;
    }
}
