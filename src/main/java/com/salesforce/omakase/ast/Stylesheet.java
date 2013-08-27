/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import static com.salesforce.omakase.emitter.EmittableRequirement.SYNTAX_TREE;

import java.util.Iterator;

import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Emittable;
import com.salesforce.omakase.parser.raw.StylesheetParser;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

/**
 * The root-level {@link Syntax} object.
 * 
 * <p>
 * Note that this will not be created unless the {@link SyntaxTree} plugin is enabled.
 * 
 * @see StylesheetParser
 * 
 * @author nmcwilliams
 */
@Emittable
@Description(broadcasted = SYNTAX_TREE)
public class Stylesheet extends AbstractSyntax implements Iterable<Statement> {
    private final SyntaxCollection<Statement> statements = StandardSyntaxCollection.create();

    /**
     * Constructs a new {@link Stylesheet} instance.
     */
    public Stylesheet() {
        super(0, 0);
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
