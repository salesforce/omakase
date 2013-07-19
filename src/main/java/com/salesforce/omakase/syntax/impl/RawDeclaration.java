/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.salesforce.omakase.syntax.Declaration;
import com.google.common.base.Objects;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class RawDeclaration extends BaseSyntaxUnit implements Declaration {
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
        this.property = checkNotNull(property, "property cannot be null");
        this.value = checkNotNull(value, "value cannot be null");
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
