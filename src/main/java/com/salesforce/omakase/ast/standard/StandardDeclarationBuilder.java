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
    /** full declaration */
    protected String content;

    /** use a {@link SyntaxFactory} instead. */
    protected StandardDeclarationBuilder() {}

    @Override
    public Declaration build() {
        return new StandardDeclaration(line, column, content);
    }

    @Override
    public DeclarationBuilder content(String content) {
        this.content = content;
        return this;
    }
}
