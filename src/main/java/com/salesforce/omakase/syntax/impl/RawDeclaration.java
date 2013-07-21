/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import com.google.common.base.Objects;
import com.salesforce.omakase.syntax.Declaration;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class RawDeclaration extends BaseSyntaxUnit implements Declaration {
    private final String property;
    private final String value;

    /**
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    public RawDeclaration(int line, int column, String property, String value) {
        super(line, column);
        this.property = property;
        this.value = value;
    }

    @Override
    public RefinedDeclaration refine() {
        return new RefinedDeclaration(this);
    }

    @Override
    public String property() {
        return property;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("property", property)
            .add("value", value)
            .add("line", line())
            .add("column", column())
            .toString();
    }
}
