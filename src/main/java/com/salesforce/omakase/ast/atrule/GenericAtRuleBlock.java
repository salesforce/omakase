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

import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.StatementIterable;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.Iterator;

/**
 * A generic wrapper containing a list of statements.
 * <p>
 * This is used for refined {@link AtRule}s (standard or custom) that contain a simple list of statements inside the block.
 *
 * @author nmcwilliams
 */
public final class GenericAtRuleBlock extends AbstractAtRuleMember implements AtRuleBlock {
    private final SyntaxCollection<StatementIterable, Statement> statements;

    /** Creates a new {@link GenericAtRuleBlock} instance with no statements or {@link Broadcaster} specified. */
    public GenericAtRuleBlock() {
        this.statements = new LinkedSyntaxCollection<StatementIterable, Statement>(this);
    }

    /**
     * Creates a new {@link GenericAtRuleBlock} instance.
     *
     * @param statements
     *     The inner {@link Statement} objects.
     * @param broadcaster
     *     Used for broadcasting new units.
     */
    public GenericAtRuleBlock(Iterable<Statement> statements, Broadcaster broadcaster) {
        this.statements = new LinkedSyntaxCollection<StatementIterable, Statement>(this, broadcaster);
        this.statements.appendAll(statements);
    }

    @Override
    public SyntaxCollection<StatementIterable, Statement> statements() {
        return statements;
    }

    @Override
    public Iterator<Statement> iterator() {
        return statements.iterator();
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        statements.propagateBroadcast(broadcaster);
        super.propagateBroadcast(broadcaster);
    }

    @Override
    public boolean isWritable() {
        return super.isWritable() && !statements.isEmptyOrNoneWritable();
    }

    @Override
    public boolean writesOwnOrphanedComments() {
        return true;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.spaceIf(!writer.isCompressed());
        appendable.append('{');
        appendable.indentIf(!writer.isCompressed());
        appendable.newlineIf(!writer.isCompressed());

        for (Statement statement : statements) {
            writer.writeInner(statement, appendable);
        }

        // custom handling of orphaned comments if they exist, because they have to go before the closing brace
        writer.appendComments(orphanedComments(), appendable);

        appendable.unindentIf(!writer.isCompressed());
        appendable.newlineIf(!writer.isCompressed());
        appendable.append('}');
    }

    @Override
    public GenericAtRuleBlock copy() {
        GenericAtRuleBlock copy = new GenericAtRuleBlock().copiedFrom(this);
        for (Statement statement : statements) {
            copy.statements().append(statement.copy());
        }
        return copy;
    }
}
