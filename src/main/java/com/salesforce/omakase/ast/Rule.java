/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import static com.salesforce.omakase.emitter.SubscribableRequirement.SYNTAX_TREE;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.plugin.standard.SyntaxTree;

/**
 * Represents a CSS Rule.
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
    public String toString() {
        return As.string(this)
            .indent()
            .add("position", super.toString())
            .add("selectors", selectors)
            .add("declarations", declarations)
            .toString();
    }
}
