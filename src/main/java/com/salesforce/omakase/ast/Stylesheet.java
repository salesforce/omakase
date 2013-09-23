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
import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.raw.StylesheetParser;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.SYNTAX_TREE;

/**
 * The root-level {@link Syntax} object.
 * <p/>
 * Note that this will not be automatically created or broadcasted unless the {@link SyntaxTree} plugin is enabled.
 * <p/>
 * Comments that appear in the original CSS source at the beginning of the stylesheet are actually going to be associated with the
 * first {@link Statement} in the sheet instead. Comments after the last {@link Statement} (or if the sheet is empty) will be
 * placed in the {@link #orphanedComments()} list.
 *
 * @author nmcwilliams
 * @see StylesheetParser
 */
@Subscribable
@Description(broadcasted = SYNTAX_TREE)
public class Stylesheet extends AbstractSyntax implements Iterable<Statement> {
    private final SyntaxCollection<Stylesheet, Statement> statements;
    private List<OrphanedComment> orphanedComments;

    /** Creates a new instance with no {@link Broadcaster} specified. Usually only used for dynamically created stylesheets. */
    public Stylesheet() {
        this(null);
    }

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

    /**
     * Adds an {@link OrphanedComment}. Orphaned comments appear after the last statement in the stylesheet.
     *
     * @param comment
     *     The comment.
     */
    public void orphanedComment(OrphanedComment comment) {
        checkNotNull(comment, "comment cannot be null");
        orphanedComments = (orphanedComments == null) ? new ArrayList<OrphanedComment>() : orphanedComments;
        orphanedComments.add(comment);
    }

    /**
     * Gets all {@link OrphanedComment}s.
     * <p/>
     * A comment is considered <em>orphaned</em> if there are no statements that follow the comment within the stylesheet.
     *
     * @return The list of comments, or an empty list if none exist.
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
            .addUnlessEmpty("orphaned", orphanedComments())
            .toString();
    }
}
