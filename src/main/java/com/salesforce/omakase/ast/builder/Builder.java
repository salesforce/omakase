/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

import com.salesforce.omakase.ast.Syntax;

/**
 * Used to construct various {@link Syntax} unit objects.
 * 
 * @author nmcwilliams
 * @param <T>
 *            The Type of {@link Syntax} object to build.
 */
public interface Builder<T extends Syntax> {
    /**
     * Specifies the line number where the {@link Syntax} unit was found.
     * 
     * @param line
     *            The line number.
     * @return this, for chaining.
     */
    Builder<T> line(int line);

    /**
     * Specifies the column number where the {@link Syntax} unit was found.
     * 
     * @param column
     *            The column number.
     * @return this, for chaining.
     */
    Builder<T> column(int column);

    /**
     * Creates a new instance of the {@link Syntax} object with the information currently given to this {@link Builder}.
     * 
     * @return The object instance.
     */
    T build();
}
