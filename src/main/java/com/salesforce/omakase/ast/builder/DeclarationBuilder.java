/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

import com.salesforce.omakase.ast.declaration.Declaration;

/**
 * A {@link Builder} used to create {@link Declaration} instances.
 * 
 * @author nmcwilliams
 */
public interface DeclarationBuilder extends Builder<Declaration> {
    /**
     * Specifies the property name of the declaration.
     * 
     * @param property
     *            The property name. This can include any associated CSS comments.
     * @return this, for chaining.
     */
    DeclarationBuilder property(String property);

    /**
     * Specifies the property value of the declaration.
     * 
     * @param value
     *            The property value. This can include any associated CSS comments.
     * @return this, for chaining.
     */
    DeclarationBuilder value(String value);
}
