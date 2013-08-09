/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.google.common.base.Objects;

/**
 * A CSS declaration, comprised of a property and value.
 * 
 * @author nmcwilliams
 */
public class Declaration extends AbstractLinkableSyntax<Declaration> implements Refinable<RefinedDeclaration>, RefinedDeclaration {
    private final RawSyntax rawProperty;
    private final RawSyntax rawValue;

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param rawProperty
     *            TODO
     * @param rawValue
     *            TODO
     */
    public Declaration(int line, int column, RawSyntax rawProperty, RawSyntax rawValue) {
        super(line, column);
        this.rawProperty = rawProperty;
        this.rawValue = rawValue;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public RawSyntax rawProperty() {
        return rawProperty;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public RawSyntax rawValue() {
        return rawValue;
    }

    @Override
    public Property property() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String value() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RefinedDeclaration refine() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String filterName() {
        return refine().property().propertyName();
    }

    @Override
    protected Declaration get() {
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("rawProperty", rawProperty)
            .add("rawValue", rawValue)
            .toString();
    }
}
