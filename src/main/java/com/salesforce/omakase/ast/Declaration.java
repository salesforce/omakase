/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.As;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * A CSS declaration, comprised of a property and value.
 * 
 * @author nmcwilliams
 */
@Subscribable
public class Declaration extends AbstractLinkableSyntax<Declaration> implements Refinable<RefinedDeclaration>,
        RefinedDeclaration {
    private final RawSyntax rawProperty;
    private final RawSyntax rawValue;

    /**
     * Creates a new instance of a {@link Declaration} with the given rawProperty (property name) and rawValue (property
     * value). The property name and value can be further refined or validated by calling {@link #refine()}.
     * 
     * <p> Note that it is called "raw" because at this point we haven't verified that either are actually valid CSS.
     * Hence really anything can technically be in there and we can't be sure it is proper formed until
     * {@link #refine()} has been called.
     * 
     * @param rawProperty
     *            The raw property name.
     * @param rawValue
     *            The raw property value.
     */
    public Declaration(RawSyntax rawProperty, RawSyntax rawValue) {
        super(rawProperty.line(), rawProperty.column());
        this.rawProperty = rawProperty;
        this.rawValue = rawValue;
    }

    /**
     * Gets the original, raw, non-validated property name.
     * 
     * @return The raw property name.
     */
    public RawSyntax rawProperty() {
        return rawProperty;
    }

    /**
     * Gets the original, raw, non-validated property value.
     * 
     * @return The raw property value.
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
        return (propertyName() != null) ? propertyName().get() : rawProperty.filterName();
    }

    @Override
    protected Declaration self() {
        return this;
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("syntax", super.toString())
            .add("rawProperty", rawProperty)
            .add("rawValue", rawValue)
            .toString();
    }
}
