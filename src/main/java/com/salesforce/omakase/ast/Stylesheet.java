/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.Iterator;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.LinkableCollection;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.plugin.SyntaxTree;

/**
 * The root-level {@link Syntax} object.
 * 
 * <p>
 * Note that this will not be created unless the {@link SyntaxTree} plugin is enabled.
 * 
 * @author nmcwilliams
 */
@Subscribable
public final class Stylesheet extends AbstractSyntax implements Iterable<Statement> {
    private final Statement head;

    /**
     * TODO
     * 
     * @param head
     *            TODO
     */
    public Stylesheet(Statement head) {
        super(head.line(), head.column());
        this.head = head;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public LinkableCollection<Statement> statements() {
        return LinkableCollection.of(head);
    }

    @Override
    public Iterator<Statement> iterator() {
        return statements().iterator();
    }

    /**
     * TODO Description
     * 
     * <p>
     * Avoid if possible, as this method is less efficient. Prefer instead to append the rule or at-rule directly to a
     * specific instance of an existing one.
     * 
     * @param statement
     *            TODO
     * @return this, for chaining.
     */
    public Stylesheet append(Statement statement) {
        Iterables.getLast(statements()).append(statement);
        return this;
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("statements", Lists.newArrayList(statements()))
            .toString();
    }

}