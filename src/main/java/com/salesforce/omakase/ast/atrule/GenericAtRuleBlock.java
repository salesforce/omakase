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

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.StatementIterable;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.Iterator;

/**
 * A generic wrapper containing a list of statements.
 * <p/>
 * This is used for refined {@link AtRule}s (standard or custom) that contain a simple list of statements inside the block.
 *
 * @author nmcwilliams
 */
public final class GenericAtRuleBlock extends AbstractSyntax<StatementIterable> implements AtRuleBlock {
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
        StyleWriter.appendComments(orphanedComments(), writer, appendable);

        appendable.unindentIf(!writer.isCompressed());
        appendable.newlineIf(!writer.isCompressed());
        appendable.append('}');
    }

    @Override
    protected GenericAtRuleBlock makeCopy(Prefix prefix, SupportMatrix support) {
        GenericAtRuleBlock copy = new GenericAtRuleBlock();
        for (Statement statement : statements) {
            copy.statements().append(statement.copy());
        }
        return copy;
    }
}
