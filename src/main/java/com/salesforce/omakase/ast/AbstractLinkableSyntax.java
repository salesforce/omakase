/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.LinkableIterator;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 * @param <T>
 *            TODO
 */
public abstract class AbstractLinkableSyntax<T extends Linkable<T>> extends AbstractSyntax implements Linkable<T> {
    private Optional<T> previous;
    private Optional<T> next;

    /**
     * Creates a new instance with the given line and column numbers.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     */
    protected AbstractLinkableSyntax(int line, int column) {
        super(line, column);
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    protected abstract T self();

    @Override
    public Optional<T> previous() {
        return previous;
    }

    @Override
    public Optional<T> next() {
        return next;
    }

    @Override
    public boolean isHead() {
        return !previous.isPresent();
    }

    @Override
    public T head() {
        return isHead() ? self() : previous.get().head();
    }

    @Override
    public boolean isTail() {
        return !next.isPresent();
    }

    @Override
    public T tail() {
        return isTail() ? self() : previous.get().tail();
    }

    @Override
    public ImmutableList<T> group() {
        return ImmutableList.copyOf(LinkableIterator.create(head()));
    }

    @Override
    public Linkable<T> append(T node) {
        checkNotNull(node, "node cannot be null");
        checkArgument(node != this, "cannot append node to itself");

        if (next.isPresent()) {
            node.append(next.get());
        }
        next = Optional.of(node);
        return this;
    }
}
