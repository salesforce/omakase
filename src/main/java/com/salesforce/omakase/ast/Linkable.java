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
    boolean isHead();

    boolean isTail();

    T head();

    T tail();

    Linkable<T> append(T node);

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
    ImmutableList<T> group();

}
