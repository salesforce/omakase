/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import static com.salesforce.omakase.emitter.SubscribableRequirement.SYNTAX_TREE;

import java.io.IOException;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * TESTME Represents a CSS Rule.
 * 
 * <p>
 * Note that {@link Rule}s will not be created unless the {@link SyntaxTree} plugin is enabled.
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(broadcasted = SYNTAX_TREE)
public class Rule extends AbstractGroupable<Statement> implements Statement {
    private final SyntaxCollection<Selector> selectors = StandardSyntaxCollection.create();
    private final SyntaxCollection<Declaration> declarations = StandardSyntaxCollection.create();

    /**
     * Creates a new {@link Rule} instance.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     */
    public Rule(int line, int column) {
        super(line, column);
    }

    /**
     * Gets the collection of selectors for this {@link Rule}.
     * 
     * @return The selectors.
     */
    public SyntaxCollection<Selector> selectors() {
        return selectors;
    }

    /**
     * Gets the collection of declarations for this {@link Rule}.
     * 
     * @return The declarations.
     */
    public SyntaxCollection<Declaration> declarations() {
        return declarations;
    }

    @Override
    protected Statement self() {
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        // selectors
        for (Selector selector : selectors) {
            selector.write(writer, appendable);
            if (!selector.isLast()) {
                appendable.append(',');
                appendable.spaceIf(!writer.isCompressed());
            }
        }

        // open declaration block
        appendable.spaceIf(!writer.isCompressed());
        appendable.append('{');
        appendable.newlineIf(writer.isVerbose());

        // declarations
        for (Declaration declaration : declarations) {
            appendable.indentIf(writer.isVerbose());
            writer.write(declaration, appendable);
            if (writer.isVerbose() || !declaration.isLast()) appendable.append(';');
            appendable.spaceIf(writer.isInline() && !declaration.isLast());
            appendable.newlineIf(writer.isVerbose());
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
