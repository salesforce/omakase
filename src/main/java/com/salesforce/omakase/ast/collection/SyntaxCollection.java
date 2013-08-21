/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.collection;

import com.salesforce.omakase.ast.Syntax;

/**
 * A collection of related {@link Syntax} units.
 * 
 * @param <T>
 *            The type of {@link Syntax} contained within the collection.
 * 
 * @author nmcwilliams
 */
public interface SyntaxCollection<T extends Syntax & Groupable<T>> extends Iterable<T> {

    /**
     * Gets the number of units in the collection.
     * 
     * @return Size of this collection.
     */
    int size();

    /**
     * Gets whether this collection contains any units.
     * 
     * @return True if there are no units in this collection.
     */
    boolean isEmpty();

    /**
     * Gets whether the given unit is contained within this collection.
     * 
     * @param unit
     *            Check if this unit is contained within this collection.
     * @return True if the unit is contained within this collection.
     */
    boolean contains(T unit);

    /**
     * Prepends the given unit the beginning of this collection.
     * 
     * @param unit
     *            The unit to prepend.
     * @return this, for chaining.
     */
    SyntaxCollection<T> prepend(T unit);

    /**
     * Prepends all of the given units to the beginning of this collection.
     * 
     * @param units
     *            The units to add.
     * @return this, for chaining.
     */
    SyntaxCollection<T> prependAll(Iterable<T> units);

    /**
     * Prepends the given unit before the given existing unit.
     * 
     * @param existing
     *            The unit to prepend.
     * @param unit
     *            Prepend this unit before the existing unit.
     * @return this, for chaining.
     * @throws IllegalArgumentException
     *             If existing is not contained within this collection.
     */
    SyntaxCollection<T> prependBefore(T existing, T unit) throws IllegalArgumentException;

    /**
     * Appends the given unit to the end of this collection.
     * 
     * @param unit
     *            The unit to append.
     * @return this, for chaining.
     */
    SyntaxCollection<T> append(T unit);

    /**
     * Appends all of the given units to the end of this collection.
     * 
     * @param units
     *            The units to append.
     * @return this, for chaining.
     */
    SyntaxCollection<T> appendAll(Iterable<T> units);

    /**
     * Appends the given unit after the given existing unit.
     * 
     * @param existing
     *            The unit that already exists in this collection.
     * @param unit
     *            The unit to append.
     * @return this, for chaining.
     * @throws IllegalArgumentException
     *             If existing is not contained within this collection.
     */
    SyntaxCollection<T> appendAfter(T existing, T unit) throws IllegalArgumentException;

    /**
     * Replaces <b>all</b> existing units with the given units.
     * 
     * @param units
     *            Replace all existing (if any) units with these.
     * @return this, for chaining.
     */
    SyntaxCollection<T> replaceExistingWith(Iterable<T> units);

    /**
     * Removes a unit from this collection. If this collection does not contain the given unit nothing will happen.
     * 
     * @param unit
     *            The unit to remove.
     * @return this, for chaining.
     */
    SyntaxCollection<T> detach(T unit);

    /**
     * Detaches <b>all</b> units from this collection.
     * 
     * @return The detached units.
     */
    Iterable<T> clear();
}
