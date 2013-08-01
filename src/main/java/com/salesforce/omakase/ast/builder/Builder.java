/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

import com.salesforce.omakase.ast.Syntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 * @param <T>
 *            TODO
 */
public interface Builder<T extends Syntax> {
    /**
     * TODO Description
     * 
     * @param line
     *            TODO
     * @return TODO
     */
    Builder<T> line(int line);

    /**
     * TODO Description
     * 
     * @param column
     *            TODO
     * @return TODO
     */
    Builder<T> column(int column);

    /**
     * TODO Description
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @return TODO
     */
    Builder<T> position(int line, int column);

    /**
     * TODO Description
     * 
     * @return TODO
     */
    T build();
}
