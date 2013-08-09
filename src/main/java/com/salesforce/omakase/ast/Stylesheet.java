/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.Iterator;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Iterators;
import com.salesforce.omakase.LinkableIterator;

/**
 * The root-level {@link Syntax} object.
 * 
 * @author nmcwilliams
 */
public final class Stylesheet extends AbstractSyntax {
    private final Statement head;

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param head
     *            TODO
     */
    public Stylesheet(int line, int column, Statement head) {
        super(line, column);
        this.head = head;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Iterator<Statement> statements() {
        return LinkableIterator.create(head);
    }

    /**
     * TODO Description
     * 
     * <p> Avoid if possible, as this method is less efficient. Prefer instead to append the rule or at-rule directly to
     * a specific instance of an existing one.
     * 
     * @param statement
     *            TODO
     * @return this, for chaining.
     */
    public Stylesheet append(Statement statement) {
        Iterators.getLast(statements()).append(statement);
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("statements", Joiner.on("\n\n").join(statements()))
            .toString();
    }
}