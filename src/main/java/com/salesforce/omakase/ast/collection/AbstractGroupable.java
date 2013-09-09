/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.collection;

import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcaster.Broadcaster;

import static com.google.common.base.Preconditions.*;

/**
 * TESTME
 * <p/>
 * Base class for {@link Groupable}s.
 *
 * @param <T>
 *     Same type as the {@link Groupable}.
 *
 * @author nmcwilliams
 */
public abstract class AbstractGroupable<T extends Syntax & Groupable<T>> extends AbstractSyntax implements Groupable<T> {
    private SyntaxCollection<T> group;

    @Override
    public boolean isFirst() {
        return !isDetached() && group.first().get().equals(this);
    }

    @Override
    public boolean isLast() {
        return !isDetached() && group.last().get().equals(this);
    }

    /** Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units). */
    public AbstractGroupable() {
        super();
    }

    /**
     * Creates a new instance with the given line and column numbers.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     */
    public AbstractGroupable(int line, int column) {
        super(line, column);
    }

    /**
     * Creates a new instance with the given line and column numbers, and the given {@link Broadcaster}.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param broadcaster
     *     Used to broadcast new units.
     */
    public AbstractGroupable(int line, int column, Broadcaster broadcaster) {
        super(line, column, broadcaster);
    }

    /**
     * Should return "this". This is needed for property type access in the {@link AbstractGroupable} class.
     *
     * @return "this".
     */
    protected abstract T self();

    @Override
    public Groupable<T> parent(SyntaxCollection<T> group) {
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
