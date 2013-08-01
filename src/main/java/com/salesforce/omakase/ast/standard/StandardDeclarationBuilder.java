/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import com.salesforce.omakase.ast.builder.DeclarationBuilder;
import com.salesforce.omakase.ast.declaration.Declaration;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StandardDeclarationBuilder extends AbstractBuilder<Declaration> implements DeclarationBuilder {
    /** TODO */
    protected String property;
    /** TODO */
    protected String value;

    /** TODO */
    protected StandardDeclarationBuilder() {}

    @Override
    public Declaration build() {
        return new StandardDeclaration(line, column, property, value);
    }

    @Override
    public DeclarationBuilder property(String property) {
        this.property = property;
        return this;
    }

    @Override
    public DeclarationBuilder value(String value) {
        this.value = value;
        return this;
    }

}
