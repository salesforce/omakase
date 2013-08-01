/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;


/**
 * TODO Description
 * 
 * @author nmcwilliams
 * @param <T>
 *            TODO
 */
public interface Refinable<T> {
    /**
     * TODO Description
     * 
     * On {@link Syntax} items, this should be the only "mutable" method that changes any of the internal state. However
     * the operation must be idempotent.
     * 
     * @return TODO
     */
    T refine();
}
