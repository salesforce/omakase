/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.collection;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

/**
 * A collection of related {@link Syntax} units.
 *
 * If you are using any of these methods in a plugin you will need to register the {@link SyntaxTree} as a dependency.
 * See {@link DependentPlugin} for more details.
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
     * TODO Description
     *
     * @return TODO
     */
    boolean isEmptyOrAllDetached();

    /**
     * Gets whether the given unit is contained within this collection.
     *
     * @param unit
     *            Check if this unit is contained within this collection.
     * @return True if the unit is contained within this collection.
     */
    boolean contains(T unit);

    /**
     * Gets the first unit in the collection.
     *
     * @return The first unit in the collection, or {@link Optional#absent()} if empty.
     */
    Optional<T> first();

    /**
     * Gets the last unit in the collection.
     *
     * @return The last unit in the collection, or {@link Optional#absent()} if empty.
     */
    Optional<T> last();

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

    /**
     * TODO Description
     *
     * @param broadcaster
     *            TODO
     * @return TODO
     */
    SyntaxCollection<T> broadcaster(Broadcaster broadcaster);

    /**
     * TODO Description
     *
     * @param broadcaster
     *            TODO
     */
    void propagateBroadcast(Broadcaster broadcaster);
}
