/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import static com.salesforce.omakase.emitter.SubscribableRequirement.SYNTAX_TREE;

import java.util.Iterator;

import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.collection.BaseSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.plugin.standard.SyntaxTree;

/**
 * The root-level {@link Syntax} object.
 * 
 * <p>
 * Note that this will not be created unless the {@link SyntaxTree} plugin is enabled.
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(broadcasted = SYNTAX_TREE)
public final class Stylesheet extends AbstractSyntax implements Iterable<Statement> {
    private final SyntaxCollection<Statement> statements = BaseSyntaxCollection.create();

    /**
     * Constructs a new {@link Stylesheet} instance.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     */
    public Stylesheet(int line, int column) {
        super(line, column);
    }

    /**
     * Gets all {@link Statement}s in this stylesheet.
     * 
     * @return All statements.
     */
    public SyntaxCollection<Statement> statements() {
        return statements;
    }

    /**
     * Appends a new {@link Statement} to the end of this stylesheet.
     * 
     * @param statement
     *            The {@link Statement} to append.
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
    public String toString() {
        return As.string(this)
            .indent()
            .add("statements", Lists.newArrayList(statements()))
            .toString();
    }
}
