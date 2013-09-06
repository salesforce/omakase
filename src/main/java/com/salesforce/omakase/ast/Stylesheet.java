/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.raw.StylesheetParser;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.Iterator;

import static com.salesforce.omakase.emitter.SubscribableRequirement.SYNTAX_TREE;

/**
 * TESTME
 * The root-level {@link Syntax} object.
 * <p/>
 * Note that this will not be created unless the {@link SyntaxTree} plugin is enabled.
 *
 * @author nmcwilliams
 * @see StylesheetParser
 */
@Subscribable
@Description(broadcasted = SYNTAX_TREE)
public class Stylesheet extends AbstractSyntax implements Iterable<Statement> {
    private final SyntaxCollection<Statement> statements;

    /**
     * Constructs a new {@link Stylesheet} instance.
     *
     * @param broadcaster
     *     Used to broadcast new units.
     */
    public Stylesheet(Broadcaster broadcaster) {
        super(1, 1, broadcaster);
        statements = StandardSyntaxCollection.create(broadcaster);
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
     *     The {@link Statement} to append.
     *
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
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        for (Statement statement : statements) {
            writer.write(statement, appendable);
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("statements", Lists.newArrayList(statements()))
            .toString();
    }
}
