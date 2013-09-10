/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.As;
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
