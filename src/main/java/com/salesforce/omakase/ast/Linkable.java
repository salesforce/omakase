/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 * @param <T>
 *            TODO
 */
public interface Linkable<T> {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    Optional<T> previous();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    Optional<T> next();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    boolean isHead();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    T head();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    boolean isTail();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    T tail();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    ImmutableList<T> group();

    /**
     * TODO Description
     * 
     * @param node
     *            TODO
     * @return TODO
     */
    Linkable<T> append(T node);
}
