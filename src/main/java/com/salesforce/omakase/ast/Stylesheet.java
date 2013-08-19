/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import static com.salesforce.omakase.emitter.SubscribableRequirement.SYNTAX_TREE;

import java.util.Iterator;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
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
    private final Statement head;

    /**
     * Constructs a new {@link Stylesheet} instance with the given first {@link Statement}.
     * 
     * @param head
     *            The first {@link Statement} in the stylesheet.
     */
    public Stylesheet(Statement head) {
        super(head.line(), head.column());
        this.head = head;
    }

    /**
     * Gets all {@link Statement}s in this stylesheet.
     * 
     * @return All statements.
     */
    public LinkableCollection<Statement> statements() {
        return LinkableCollection.of(head);
    }

    /**
     * Appends a new {@link Statement} to the end of this stylesheet.
     * 
     * <p>
     * Avoid if possible, as this method is less efficient. Prefer instead to append the rule or at-rule directly to a
     * specific instance of an existing one.
     * 
     * @param statement
     *            The {@link Statement} to append.
     * @return this, for chaining.
     */
    public Stylesheet append(Statement statement) {
        Iterables.getLast(statements()).append(statement);
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
