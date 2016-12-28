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

import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.AUTOMATIC;

/**
 * Represents a CSS Rule.
 * <p>
 * You might be looking for a "DeclarationBlock" class. Currently such a class serves no purpose, and all ordered declarations are
 * contained inside of a {@link SyntaxCollection} within this class instead.
 * <p>
 * Note that if a {@link Rule} does not have any selectors or declarations (or all of it's selectors and declarations are
 * <em>detached</em>) then the rule will not be printed out.
 * <p>
 * Comments that appear in the original CSS source "before" the rule are actually going to be added to the first {@link Selector}
 * instead of the rule. However, for convenience, the getter annotation-related methods will include results from the first
 * selector (getter methods for pure comments will not do this).
 * <p>
 * Any comments that appear after the semi-colon of the last rule are considered orphaned comments and can be retrieved via {@link
 * #orphanedComments()}. Note that any comments before the semi-colon (or if the last declaration does not end with a semi-colon)
 * are attributed as orphaned comments on the {@link Declaration} instead.
 * <p>
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
        this(-1, -1);
    }

    /**
     * Creates a new {@link Rule} instance with the given line and column numbers.
     *  @param line
     *     The line number.
     * @param column
     *     The column number.
     */
    public Rule(int line, int column) {
        super(line, column);
        selectors = new LinkedSyntaxCollection<>(this);
        declarations = new LinkedSyntaxCollection<>(this);
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
    protected Rule self() {
        return this;
    }

    @Override
    public boolean hasAnnotation(String name) {
        return super.hasAnnotation(name) ||
            (selectors.first().isPresent() && selectors.first().get().hasAnnotation(name));
    }

    @Override
    public boolean hasAnnotation(CssAnnotation annotation) {
        return super.hasAnnotation(annotation) ||
            (selectors.first().isPresent() && selectors.first().get().hasAnnotation(annotation));
    }

    @Override
    public Optional<CssAnnotation> annotation(String name) {
        Optional<CssAnnotation> annotation = super.annotation(name);
        if (annotation.isPresent()) {
            return annotation;
        }
        return selectors.first().isPresent() ? selectors.first().get().annotation(name) : Optional.empty();
    }

    @Override
    public List<CssAnnotation> annotations() {
        List<CssAnnotation> annotations = super.annotations();
        if (selectors.first().isPresent()) {
            annotations.addAll(selectors.first().get().annotations());
        }
        return annotations;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster, Status status) {
        if (this.status() == status) {
            selectors.propagateBroadcast(broadcaster, status);
            declarations.propagateBroadcast(broadcaster, status);
            super.propagateBroadcast(broadcaster, status);
        }
    }

    @Override
    public boolean isWritable() {
        // don't write out rules with no selectors/declarations or all detached selectors/declarations
        return super.isWritable() && !selectors.isEmptyOrNoneWritable() && !declarations.isEmptyOrNoneWritable();
    }

    @Override
    public boolean writesOwnOrphanedComments() {
        return true;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        // newlines (unless first statement)
        if (!writer.isCompressed() && !writer.isFirstAtCurrentDepth()) {
            appendable.newline().newlineIf(writer.isVerbose());
        }

        // selectors
        for (Selector selector : selectors) {
            writer.writeInner(selector, appendable);
        }

        // open declaration block
        appendable.spaceIf(!writer.isCompressed());
        appendable.append('{');
        appendable.indentIf(writer.isVerbose());
        appendable.newlineIf(writer.isVerbose());
        writer.incrementDepth();

        // declarations
        for (Declaration declaration : declarations) {
            writer.writeInner(declaration, appendable);
        }

        if (writer.isVerbose()) appendable.append(';');

        // custom handling of orphaned comments if they exist, because they have to go before the closing brace
        writer.appendComments(orphanedComments(), appendable);

        // close declaration block
        writer.decrementDepth();
        appendable.unindentIf(writer.isVerbose());
        appendable.newlineIf(writer.isVerbose());
        appendable.append('}');
    }

    @Override
    public Rule copy() {
        Rule copy = new Rule().copiedFrom(this);
        for (Selector selector : selectors) {
            copy.selectors().append(selector.copy());
        }
        for (Declaration declaration : declarations) {
            copy.declarations().append(declaration.copy());
        }
        return copy;
    }

    @Override
    public void destroy() {
        super.destroy();
        selectors.destroyAll();
        declarations.destroyAll();
    }
}
