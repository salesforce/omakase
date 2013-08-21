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
public interface SyntaxCollection<T extends Syntax> extends Iterable<T> {

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
     * TODO Description
     * 
     * @param existingunit
     * @param newunit
     * @return
     */
    SyntaxCollection<T> prependBefore(T existingunit, T newunit);

    /**
     * TODO Description
     * 
     * @param unit
     * @return
     */
    SyntaxCollection<T> append(T unit);

    /**
     * TODO Description
     * 
     * @param units
     * @return
     */
    SyntaxCollection<T> appendAll(Iterable<T> units);

    /**
     * TODO Description
     * 
     * @param existingunit
     * @param newunit
     * @return
     */
    SyntaxCollection<T> appendAfter(T existingunit, T newunit);

    /**
     * TODO Description
     * 
     * @param units
     * @return
     */
    SyntaxCollection<T> replaceExistingWith(Iterable<T> units);

    /**
     * TODO Description
     * 
     * @param unit
     * @return
     */
    SyntaxCollection<T> detach(T unit);

    /**
     * TODO Description
     * 
     * @return
     */
    SyntaxCollection<T> clear();

}
