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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.raw.StylesheetParser;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.*;
import static com.salesforce.omakase.emitter.SubscribableRequirement.SYNTAX_TREE;

/**
 * TESTME
 * <p/>
 * The root-level {@link Syntax} object.
 * <p/>
 * Note that this will not be created unless the {@link SyntaxTree} plugin is enabled.
 * <p/>
 * Adding or retrieving comments delegates to the first statement in the stylesheet.
 *
 * @author nmcwilliams
 * @see StylesheetParser
 */
@Subscribable
@Description(broadcasted = SYNTAX_TREE)
public class Stylesheet extends AbstractSyntax implements Iterable<Statement> {
    private final SyntaxCollection<Stylesheet, Statement> statements;
    private List<OrphanedComment> orphanedComments;

    /**
     * Constructs a new {@link Stylesheet} instance.
     *
     * @param broadcaster
     *     Used to broadcast new units.
     */
    public Stylesheet(Broadcaster broadcaster) {
        super(1, 1, broadcaster);
        statements = StandardSyntaxCollection.create(this, broadcaster);
    }

    /**
     * Gets all {@link Statement}s in this stylesheet.
     *
     * @return All statements.
     */
    public SyntaxCollection<Stylesheet, Statement> statements() {
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
        return statements().iterator();
    }

    @Override
    public void comments(Iterable<String> commentsToAdd) {
        checkState(!statements.isEmpty(), "cannot add a comment to a stylesheet without at least one statement");
        Iterables.get(statements, 0).comments(commentsToAdd);
    }

    @Override
    public List<Comment> comments() {
        if (statements.isEmpty()) return ImmutableList.of();
        return Iterables.get(statements, 0).comments();
    }

    /**
     * Adds an {@link OrphanedComment}.
     *
     * @param comment
     *     The comment.
     */
    public void orphanedComment(OrphanedComment comment) {
        checkNotNull(comment, "comment cannot be null");
        checkArgument(comment.location() == OrphanedComment.Location.STYLESHEET, "invalid orphaned value location");
        orphanedComments = (orphanedComments == null) ? new ArrayList<OrphanedComment>() : orphanedComments;
        orphanedComments.add(comment);
    }

    /**
     * Gets all {@link OrphanedComment}s.
     * <p/>
     * A comment is considered <em>orphaned</em> if it does not appear before a logically associated unit. For example, comments
     * at the end of a stylesheet or declaration block.
     *
     * @return The list of comments, or an empty list if none are specified.
     */
    public List<OrphanedComment> orphanedComments() {
        return orphanedComments == null ? ImmutableList.<OrphanedComment>of() : ImmutableList.copyOf(orphanedComments);
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        for (Statement statement : statements) {
            writer.write(statement, appendable);
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("statements", Lists.newArrayList(statements()))
            .add("orphaned", orphanedComments())
            .toString();
    }
}
