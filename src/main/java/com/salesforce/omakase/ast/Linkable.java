/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.plugin.Plugin;

/**
 * The interface for related {@link Syntax} units grouped together in a chain, for example {@link Selector}s or
 * {@link Declaration}s.
 * 
 * <p>
 * This type of custom structure is chosen for several reasons. First, it gives consideration to an expected level of
 * random point insertions (via rework {@link Plugin}s and other modifications).
 * 
 * <p>
 * Second, due to the very open nature of {@link Plugin}s and how they work, combined with the ability for updates,
 * insertions, etc... it would be non-trivial to maintain a correct state across all objects (for example, a Rule
 * maintaining its list of declarations would need to be updated when that list of declarations is modified). Certainly
 * this <em>could</em> be done, allowing for more traditional Collection objects to be used instead, however it would
 * require changes imposing on how {@link Plugin}s are allowed to do their work.
 * 
 * <p>
 * Iteration order is in most cases expected to be trivial (after all, how many declarations is one rule going to
 * have?). However there are some operations that, due to the nature of this structure, require rewinding all the way to
 * the beginning or moving to the end of the chain, which is not the most efficient. These methods are duly noted.
 * 
 * @author nmcwilliams
 * @param <T>
 *            The Type of the items linked together.
 */
public interface Linkable<T> extends Syntax {
    /**
     * Gets whether there is a previous node present.
     * 
     * @return True if there is a previous node.
     */
    boolean hasPrevious();

    /**
     * Gets the previous node.
     * 
     * @return The previous node, or {@link Optional#absent()} if not set.
     */
    Optional<T> previous();

    /**
     * Gets whether there is a next node present.
     * 
     * @return True if there is a next node.
     */
    boolean hasNext();

    /**
     * Gets the next node.
     * 
     * @return The next node, or {@link Optional#absent()} if not set.
     */
    Optional<T> next();

    /**
     * Gets whether this node is the first one in the group (i.e., {@link #hasPrevious()} is false).
     * 
     * @return True if no previous node exists.
     */
    boolean isHead();

    /**
     * Gets the head node connected to this one. In other words, this will continually calling {@link #previous()} until
     * the head node is found. Use sparingly.
     * 
     * @return The head node.
     */
    T head();

    /**
     * Gets whether this node is the last one in the group (i.e., {@link #hasNext()} is false).
     * 
     * @return True if no next node exists.
     */
    boolean isTail();

    /**
     * Gets the tail node connected to this one. In other words, this will continually call {@link #next()} until the
     * last node is found. Use sparingly.
     * 
     * @return The tail node.
     */
    T tail();

    /**
     * Gathers all nodes connected to this one (behind and ahead) into one ordered list. Use sparingly.
     * 
     * @return The list of all connected nodes ordered from first to last.
     */
    ImmutableList<T> group();

    /**
     * Appends the given node to this one.
     * 
     * <p>
     * If the next node is defined for this node, the next node will be attached to the <em>end</em> of the given node
     * (which means the given node may have it's own "next", which will continue to maintain its position with respect
     * to the given node).
     * 
     * <p>
     * The previous node of the given node will be set to this node (which means if the given node has a previous node
     * already defined, it will be <strong>replaced</strong> with this one).
     * 
     * @param node
     *            The {@link Linkable} to append.
     * @return this, for chaining.
     */
    Linkable<T> append(T node);
}
