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
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
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
@Description(broadcasted = AUTOMATIC)
public class Rule extends AbstractGroupable<Stylesheet, Statement> implements Statement {
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
    public void propagateBroadcast(Broadcaster broadcaster) {
        super.propagateBroadcast(broadcaster);
        selectors.propagateBroadcast(broadcaster);
        declarations.propagateBroadcast(broadcaster);
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
        // newlines (unless first statement)
        if (!writer.isCompressed() && !isFirst()) {
            appendable.newline();
            appendable.newlineIf(writer.isVerbose());
        }

        // selectors
        for (Selector selector : selectors) {
            if (selector.isWritable()) {
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
            if (declaration.isWritable()) {
                appendable.indentIf(writer.isVerbose());
                writer.write(declaration, appendable);
                if (writer.isVerbose() || !declaration.isLast()) appendable.append(';');
                appendable.spaceIf(writer.isInline() && !declaration.isLast());
                appendable.newlineIf(writer.isVerbose());
            }
        }

        // close declaration block
        appendable.append('}');
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("abstract", super.toString())
            .add("selectors", selectors)
            .add("declarations", declarations)
            .addUnlessEmpty("orphaned", orphanedComments())
            .toString();
    }
}
