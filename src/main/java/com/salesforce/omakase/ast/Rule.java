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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.*;
import static com.salesforce.omakase.emitter.SubscribableRequirement.SYNTAX_TREE;

/**
 * Represents a CSS Rule.
 * <p/>
 * Note that this will not be automatically created or broadcasted unless the {@link SyntaxTree} plugin is enabled.
 * <p/>
 * You might be looking for a "DeclarationBlock" class. Currently such a class serves no purpose, and all ordered declarations are
 * contained inside of a {@link SyntaxCollection} within this class instead.
 * <p/>
 * Note that if a {@link Rule} does not have any selectors or declarations (or all of it's selectors and declarations are
 * <em>detached</em>) then the rule will not be printed out.
 * <p/>
 * Comments that appear in the original CSS source "before" the rule are actually going to be added to the first {@link Selector}
 * instead of the rule.
 * <p/>
 * Any comments that appear after the semi-colon of the last rule are considered orphaned comments and can be retrieved via {@link
 * #orphanedComments()}. Note that any comments before the semi-colon (or if the last declaration does not end with a semi-colon)
 * are attributed as orphaned comments on the {@link Declaration} instead.
 * <p/>
 * Example of a dynamically created rule:
 * <pre>
 * Rule rule = new Rule();
 * rule.selectors().append(new Selector(new ClassSelector("class")));
 * rule.selectors().append(new Selector(new IdSelector("id")));
 * rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
 * rule.declarations().append(new Declaration(Property.MARGIN, NumericalValue.of(5, "px")));
 * </pre>
 *
 * @author nmcwilliams
 */

@Subscribable
@Description(broadcasted = SYNTAX_TREE)
public class Rule extends AbstractGroupable<Stylesheet, Statement> implements Statement {
    private final SyntaxCollection<Rule, Selector> selectors;
    private final SyntaxCollection<Rule, Declaration> declarations;
    private List<OrphanedComment> orphanedComments;

    /** Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units). */
    public Rule() {
        this(-1, -1, null);
    }

    /**
     * Creates a new {@link Rule} instance with the given line and column numbers.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param broadcaster
     *     Used to broadcast new units.
     */
    public Rule(int line, int column, Broadcaster broadcaster) {
        super(line, column, broadcaster);
        selectors = StandardSyntaxCollection.create(this, broadcaster);
        declarations = StandardSyntaxCollection.create(this, broadcaster);
    }

    /**
     * Gets the collection of selectors for this {@link Rule}. You can append, prepend, etc... additional {@link Selector}s to
     * this collection. New {@link Selector}s will automatically be broadcasted.
     *
     * @return The selectors.
     */
    public SyntaxCollection<Rule, Selector> selectors() {
        return selectors;
    }

    /**
     * Gets the collection of declarations for this {@link Rule}. You can append, prepend, etc... additional {@link Declaration}s
     * to this collection. New {@link Declaration}s will be automatically broadcasted.
     *
     * @return The declarations.
     */
    public SyntaxCollection<Rule, Declaration> declarations() {
        return declarations;
    }

    @Override
    public Syntax broadcaster(Broadcaster broadcaster) {
        selectors.broadcaster(broadcaster);
        declarations.broadcaster(broadcaster);
        return super.broadcaster(broadcaster);
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        super.propagateBroadcast(broadcaster);
        selectors.propagateBroadcast(broadcaster);
        declarations.propagateBroadcast(broadcaster);
    }

    /**
     * Adds an {@link OrphanedComment}.
     *
     * @param comment
     *     The comment.
     */
    public void orphanedComment(OrphanedComment comment) {
        checkNotNull(comment, "comment cannot be null");
        checkArgument(comment.location() == OrphanedComment.Location.RULE, "invalid orphaned value location");
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
    public Optional<Rule> asRule() {
        return Optional.of(this);
    }

    @Override
    public Optional<AtRule> asAtRule() {
        return Optional.absent();
    }

    @Override
    protected Statement self() {
        return this;
    }

    @Override
    public boolean isWritable() {
        // don't write out rules with no selectors or all detached selectors
        return !isDetached() && !selectors.isEmptyOrAllDetached() && !declarations().isEmptyOrAllDetached();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        // selectors
        for (Selector selector : selectors) {
            if (!selector.isDetached()) {
                writer.write(selector, appendable);
                if (!selector.isLast()) {
                    appendable.append(',');
                    appendable.spaceIf(!writer.isCompressed());
                }
            }
        }

        // open declaration block
        appendable.spaceIf(!writer.isCompressed());
        appendable.append('{');
        appendable.newlineIf(writer.isVerbose());

        // declarations
        for (Declaration declaration : declarations) {
            if (!declaration.isDetached()) {
                appendable.indentIf(writer.isVerbose());
                writer.write(declaration, appendable);
                if (writer.isVerbose() || !declaration.isLast() && !declaration.isDetached()) appendable.append(';');
                appendable.spaceIf(writer.isInline() && !declaration.isLast());
                appendable.newlineIf(writer.isVerbose());
            }
        }

        // close declaration block
        appendable.append('}');

        // newlines (unless last statement)
        if (!writer.isCompressed() && !isLast()) {
            appendable.newline();
            appendable.newlineIf(writer.isVerbose());
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("abstract", super.toString())
            .add("selectors", selectors)
            .add("declarations", declarations)
            .add("orphaned", orphanedComments())
            .toString();
    }
}
