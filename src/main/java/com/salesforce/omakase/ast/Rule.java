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
import com.google.common.collect.Iterables;
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
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static com.salesforce.omakase.emitter.SubscribableRequirement.SYNTAX_TREE;

/**
 * TESTME
 * <p/>
 * Represents a CSS Rule.
 * <p/>
 * Note that {@link Rule}s will not be created unless the {@link SyntaxTree} plugin is enabled.
 * <p/>
 * You might be looking for a "DeclarationBlock" class. Currently such a class serves no purpose, and all ordered declarations are
 * contained inside of a {@link SyntaxCollection} within this class instead.
 * <p/>
 * Adding or retrieving comments delegates to the first selector in the rule.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(broadcasted = SYNTAX_TREE)
public class Rule extends AbstractGroupable<Statement> implements Statement {
    private final SyntaxCollection<Selector> selectors;
    private final SyntaxCollection<Declaration> declarations;

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
        selectors = StandardSyntaxCollection.create(broadcaster);
        declarations = StandardSyntaxCollection.create(broadcaster);
    }

    /**
     * Gets the collection of selectors for this {@link Rule}. You can append, prepend, etc... additional {@link Selector}s to
     * this collection. New {@link Selector}s will automatically be broadcasted.
     *
     * @return The selectors.
     */
    public SyntaxCollection<Selector> selectors() {
        return selectors;
    }

    /**
     * Gets the collection of declarations for this {@link Rule}. You can append, prepend, etc... additional {@link Declaration}s
     * to this collection. New {@link Declaration}s will be automatically broadcasted.
     *
     * @return The declarations.
     */
    public SyntaxCollection<Declaration> declarations() {
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

    @Override
    public void comments(Iterable<String> commentsToAdd) {
        checkState(!selectors.isEmpty(), "cannot add a comment to a stylesheet without at least one selector");
        Iterables.get(selectors, 0).comments(commentsToAdd);
    }

    @Override
    public List<Comment> comments() {
        if (selectors.isEmpty()) return ImmutableList.of();
        return Iterables.get(selectors, 0).comments();
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
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        // don't write out rules with no selectors or all detached selectors
        if (isDetached() || selectors.isEmptyOrAllDetached()) return;

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
            .add("position", super.toString())
            .add("selectors", selectors)
            .add("declarations", declarations)
            .toString();
    }
}
