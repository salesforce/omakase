/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.salesforce.omakase.ast.AbstractSyntax;

/**
 * TODO Description
 * 
 * @param <T>
 *            TODO
 * @author nmcwilliams
 */
public abstract class AbstractGroupable<T> extends AbstractSyntax implements Groupable<T> {
    SyntaxCollection<T> group;

    /**
     * Creates a new instance with the given line and column numbers.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     */
    public AbstractGroupable(int line, int column) {
        super(line, column);
    }

    /**
     * Should return "this". This is needed for property type access in the {@link AbstractGroupable} class.
     * 
     * @return "this".
     */
    protected abstract T self();

    /**
     * TODO Description
     * 
     * @param group
     *            TODO
     * @return TODO
     */
    public Groupable<T> group(SyntaxCollection<T> group) {
        this.group = group;
        return this;
    }

    @Override
    public SyntaxCollection<T> group() {
        checkState(!isDetached(), "currently not part of any group!");
        return group;
    }

    @Override
    public Groupable<T> prepend(T unit) {
        checkNotNull(unit, "unit cannot be null");
        checkState(!isDetached(), "currently not part of any group!");
        group.prependBefore(self(), unit);
        return this;
    }

    @Override
    public Groupable<T> append(T unit) {
        checkNotNull(unit, "unit cannot be null");
        checkState(!isDetached(), "currently not part of any group!");
        group.appendAfter(self(), unit);
        return this;
    }

    @Override
    public void detach() {
        group.detach(self());
        this.group = null;
    }

    @Override
    public boolean isDetached() {
        return group == null;
    }

}
