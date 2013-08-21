/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.collection;


/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Groupable<T> {
    SyntaxCollection<T> group();

    Groupable<T> prepend(T unit);

    Groupable<T> append(T unit);

    void detach();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    boolean isDetached();
}
