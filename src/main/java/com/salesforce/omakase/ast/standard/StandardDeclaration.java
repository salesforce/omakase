/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import com.google.common.base.Objects;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.RefinedDeclaration;

/**
 * Standard implementation of a {@link Declaration}.
 * 
 * <p> Not intended for subclassing or direct reference by clients.
 * 
 * @author nmcwilliams
 */
final class StandardDeclaration extends AbstractSyntax implements RefinedDeclaration {
    private final String property;
    private final String value;

    StandardDeclaration(int line, int column, String property, String value) {
        super(line, column);
        this.property = property;
        this.value = value;
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
    public RefinedDeclaration refine() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("property", property)
            .add("value", value)
            .toString();
    }
}
