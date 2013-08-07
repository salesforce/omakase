/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.List;

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
     * @param previous
     *            TODO
     * @return TODO
     */
    Linkable<T> previous(T previous);

    /**
     * TODO Description
     * 
     * @return TODO
     */
    T previous();

    /**
     * TODO Description
     * 
     * @param next
     *            TODO
     * @return TODO
     */
    Linkable<T> next(T next);

    /**
     * TODO Description
     * 
     * @return TODO
     */
    T next();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    List<T> group();
}
