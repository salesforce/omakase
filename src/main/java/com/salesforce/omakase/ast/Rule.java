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
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.AUTOMATIC;

/**
 * Represents a CSS Rule.
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
 * <pre><code>
 * Rule rule = new Rule();
 * rule.selectors().append(new Selector(new ClassSelector("class")));
 * rule.selectors().append(new Selector(new IdSelector("id")));
 * rule.declarations().append(new Declaration(Property.DISPLAY, KeywordValue.of(Keyword.NONE)));
 * rule.declarations().append(new Declaration(Property.MARGIN, NumericalValue.of(5, "px")));
 * </code></pre>
 *
 * @author nmcwilliams
 */

@Subscribable
@Description(broadcasted = AUTOMATIC)
public final class Rule extends AbstractGroupable<StatementIterable, Statement> implements Statement {
    private final SyntaxCollection<Rule, Selector> selectors;
    private final SyntaxCollection<Rule, Declaration> declarations;

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
        super(line, column);
        selectors = new LinkedSyntaxCollection<Rule, Selector>(this, broadcaster);
        declarations = new LinkedSyntaxCollection<Rule, Declaration>(this, broadcaster);
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
    public Optional<Rule> asRule() {
        return Optional.of(this);
    }

    @Override
    public Optional<AtRule> asAtRule() {
        return Optional.absent();
    }

    @Override
    protected Rule self() {
        return this;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        super.propagateBroadcast(broadcaster);
        selectors.propagateBroadcast(broadcaster);
        declarations.propagateBroadcast(broadcaster);
    }

    @Override
    public boolean isWritable() {
        // don't write out rules with no selectors or all detached selectors
        return !selectors.isEmpty() && !declarations.isEmpty();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        // newlines (unless first statement)
        if (!writer.isCompressed() && !isFirst()) {
            appendable.newline();
            appendable.newlineIf(writer.isVerbose());
        }

        boolean wroteFirst = false;

        // selectors
        for (Selector selector : selectors) {
            if (selector.isWritable()) {
                if (wroteFirst) {
                    appendable.append(',');
                    appendable.spaceIf(!writer.isCompressed());
                }
                writer.writeInner(selector, appendable);
                wroteFirst = true;
            }
        }

        // open declaration block
        appendable.spaceIf(!writer.isCompressed());
        appendable.append('{');
        appendable.indentIf(writer.isVerbose());

        // declarations
        wroteFirst = false;
        for (Declaration declaration : declarations) {
            if (declaration.isWritable()) {

                if (wroteFirst) {
                    appendable.append(';');
                }

                if (writer.isVerbose()) {
                    appendable.newline();
                } else if (writer.isInline() && wroteFirst) {
                    appendable.space();
                }

                writer.writeInner(declaration, appendable);
                wroteFirst = true;
            }
        }

        if (wroteFirst && writer.isVerbose()) appendable.append(';');

        // close declaration block
        appendable.unindentIf(writer.isVerbose());
        appendable.newlineIf(writer.isVerbose());
        appendable.append('}');
    }

    @Override
    protected Rule makeCopy(Prefix prefix, SupportMatrix support) {
        Rule copy = new Rule();
        for (Selector selector : selectors) {
            copy.selectors().append(selector.copy(prefix, support));
        }
        for (Declaration declaration : declarations) {
            copy.declarations().append(declaration.copy(prefix, support));
        }
        return copy;
    }
}
