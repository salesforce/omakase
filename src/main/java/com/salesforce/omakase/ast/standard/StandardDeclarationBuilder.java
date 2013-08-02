/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import com.salesforce.omakase.ast.builder.Builder;
import com.salesforce.omakase.ast.builder.DeclarationBuilder;
import com.salesforce.omakase.ast.builder.SyntaxFactory;
import com.salesforce.omakase.ast.declaration.Declaration;

/**
 * A {@link Builder} used to create {@link Declaration} instances.
 * 
 * @author nmcwilliams
 */
public class StandardDeclarationBuilder extends AbstractBuilder<Declaration> implements DeclarationBuilder {
    /** property name */
    protected String property;
    /** property value */
    protected String value;

    /** use a {@link SyntaxFactory} instead. */
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
