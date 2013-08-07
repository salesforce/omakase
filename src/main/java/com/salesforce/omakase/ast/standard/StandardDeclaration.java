/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import com.google.common.base.Objects;
import com.salesforce.omakase.ast.Declaration;
import com.salesforce.omakase.ast.Property;
import com.salesforce.omakase.ast.RefinedDeclaration;

/**
 * Standard implementation of a {@link Declaration}.
 * 
 * <p> Not intended for subclassing or direct reference by clients.
 * 
 * @author nmcwilliams
 */
final class StandardDeclaration extends AbstractLinkableSyntax<Declaration> implements RefinedDeclaration {
    private final String original;
    private Property property;
    private String value;

    StandardDeclaration(int line, int column, String original) {
        super(line, column);
        this.original = original;
    }

    @Override
    public String original() {
        return original;
    }

    @Override
    public RefinedDeclaration property(Property property) {
        this.property = property;
        return this;
    }

    @Override
    public Property property() {
        return property;
    }

    @Override
    public RefinedDeclaration value(String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public RefinedDeclaration refine() {
        if (property == null) {
            // TODO assign property and value
        }
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("raw", original)
            .add("property", property)
            .add("value", value)
            .toString();
    }

}
