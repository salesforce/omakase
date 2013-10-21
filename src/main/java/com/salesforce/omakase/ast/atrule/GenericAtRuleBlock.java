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

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * A generic wrapper containing a list of statements. This is used for refined {@link AtRule}s (standard or custom) that contain a
 * simple list of statements inside the block.
 * <p/>
 * TODO no parent?
 *
 * @author nmcwilliams
 */
public final class GenericAtRuleBlock extends AbstractSyntax implements AtRuleBlock {
    private final SyntaxCollection<Stylesheet, Statement> statements;

    /**
     * Creates a new {@link GenericAtRuleBlock} instance. Be sure to call {@link #propagateBroadcast(Broadcaster)} as soon as a
     * broadcaster is available.
     *
     * @param parent
     *     The parent stylesheet.
     */
    public GenericAtRuleBlock(Stylesheet parent) {
        this(parent, ImmutableList.<Statement>of(), null);
    }

    /**
     * Creates a new {@link GenericAtRuleBlock} instance.
     *
     * @param parent
     *     The {@link Stylesheet} that contains the {@link AtRule} that owns this block.
     * @param statements
     *     The inner {@link Statement} objects.
     * @param broadcaster
     *     Used for broadcasting new units.
     */
    public GenericAtRuleBlock(Stylesheet parent, Iterable<Statement> statements, Broadcaster broadcaster) {
        this.statements = StandardSyntaxCollection.create(parent, broadcaster);
        this.statements.appendAll(statements);
    }

    /**
     * Creates a new {@link GenericAtRuleBlock} instance.
     *
     * @param statements
     *     The collection of inner statements.
     */
    public GenericAtRuleBlock(SyntaxCollection<Stylesheet, Statement> statements) {
        this.statements = statements;
    }

    /**
     * Gets the {@link SyntaxCollection} of statements within this block.
     *
     * @return The collection of statements within this block.
     */
    public SyntaxCollection<Stylesheet, Statement> statements() {
        return statements;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        super.propagateBroadcast(broadcaster);
        statements.propagateBroadcast(broadcaster);
    }

    @Override
    public boolean isWritable() {
        return !statements.isEmptyOrNoneWritable();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.spaceIf(!writer.isCompressed());
        appendable.append('{');
        appendable.newlineIf(!writer.isCompressed());
        for (Statement statement : statements) {
            writer.writeInner(statement, appendable);
        }
        appendable.newlineIf(!writer.isCompressed());
        appendable.append('}');
    }
}
