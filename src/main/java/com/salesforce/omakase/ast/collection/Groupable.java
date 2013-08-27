/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.collection;

import com.salesforce.omakase.ast.Syntax;

/**
 * Represents an item that appears in a group or chain of other related units, for usage with {@link SyntaxCollection}.
 * 
 * @param <T>
 *            The type of units to be grouped with.
 * 
 * @see SyntaxCollection
 * @author nmcwilliams
 */
public interface Groupable<T extends Syntax & Groupable<T>> {

    /**
     * TODO Description
     * 
     * @return TODO
     */
    boolean isFirst();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    boolean isLast();

    /**
     * Gets the parent {@link SyntaxCollection} this unit belongs to.
     * 
     * @return The parent {@link SyntaxCollection}.
     * 
     * @throws IllegalStateException
     *             If this unit is currently detached (doesn't belong to any group).
     */
    SyntaxCollection<T> group();

    /**
     * Prepends the given unit before this one.
     * 
     * @param unit
     *            The unit to prepend.
     * @return this, for chaining.
     * 
     * @throws IllegalStateException
     *             If this unit is currently detached (doesn't belong to any group).
     */
    Groupable<T> prepend(T unit);

    /**
     * Appends the given unit after this one.
     * 
     * @param unit
     *            The unit to append.
     * @return this, for chaining.
     * 
     * @throws IllegalStateException
     *             If this unit is currently detached (doesn't belong to any group).
     */
    Groupable<T> append(T unit);

    /**
     * Detaches (removes) this unit from the parent {@link SyntaxCollection}.
     */
    void detach();

    /**
     * Gets whether this unit is detached.
     * 
     * @return True if this unit is detached.
     */
    boolean isDetached();

    /**
     * Sets the parent group. This should only be called internally... calling it yourself may result in expected
     * behavior.
     * 
     * @param group
     *            The parent group.
     * @return this, for chaining.
     */
    Groupable<T> parent(SyntaxCollection<T> group);
}
