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
     * @param rawProperty
     *            TODO
     * @param rawValue
     *            TODO
     */
    public Declaration(RawSyntax rawProperty, RawSyntax rawValue) {
        super(rawProperty.line(), rawProperty.column());
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
    public PropertyName propertyName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PropertyValue propertyValue() {
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
        return refine().propertyName().get();
    }

    @Override
    protected Declaration self() {
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
