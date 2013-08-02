/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.builder.Builder;

/**
 * Base class for {@link Builder}s.
 * 
 * @param <T>
 *            The Type of {@link Syntax} object to build.
 * 
 * @author nmcwilliams
 */
public abstract class AbstractBuilder<T extends Syntax> implements Builder<T> {
    /** line number */
    protected int line = -1;
    /** column number */
    protected int column = -1;

    @Override
    public Builder<T> line(int line) {
        this.line = line;
        return this;
    }

    @Override
    public Builder<T> column(int column) {
        this.column = column;
        return this;
    }
}
