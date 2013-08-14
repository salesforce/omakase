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
 * Base class for {@link Linkable} {@link Syntax} units.
 * 
 * @author nmcwilliams
 * @param <T>
 *            Same type as the {@link Linkable}.
 */
public abstract class AbstractLinkable<T extends Linkable<T>> extends AbstractSyntax implements Linkable<T> {
    private Optional<T> previous = Optional.absent();
    private Optional<T> next = Optional.absent();

    /**
     * Creates a new instance with the given line and column numbers.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     */
    public AbstractLinkable(int line, int column) {
        super(line, column);
    }

    /**
     * Should return "this". This is needed for property type access in the {@link AbstractLinkable} class.
     * 
     * @return "this".
     */
    protected abstract T self();

    @Override
    public boolean hasPrevious() {
        return previous.isPresent();
    }

    @Override
    public Optional<T> previous() {
        return previous;
    }

    @Override
    public boolean hasNext() {
        return next.isPresent();
    }

    @Override
    public Optional<T> next() {
        return next;
    }

    @Override
    public boolean isHead() {
        return !hasPrevious();
    }

    @Override
    public T head() {
        return isHead() ? self() : previous.get().head();
    }

    @Override
    public boolean isTail() {
        return !hasNext();
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
