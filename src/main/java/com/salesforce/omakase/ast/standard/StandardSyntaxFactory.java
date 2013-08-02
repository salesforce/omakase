/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.builder.*;

/**
 * A {@link SyntaxFactory} factory for creating standard {@link Syntax} objects.
 * 
 * @author nmcwilliams
 */
public class StandardSyntaxFactory implements SyntaxFactory {
    private static final StandardSyntaxFactory instance = new StandardSyntaxFactory();

    /**
     * Gets the cached factory instance.
     * 
     * @return The cache instance.
     */
    public static SyntaxFactory instance() {
        return instance;
    }

    /** Only here to allow for subclassing. Clients should use {@link #instance()} instead. */
    protected StandardSyntaxFactory() {}

    @Override
    public StylesheetBuilder stylesheet() {
        return new StandardStylesheetBuilder();
    }

    @Override
    public RuleBuilder rule() {
        return new StandardRuleBuilder();
    }

    @Override
    public SelectorGroupBuilder selectorGroup() {
        return new StandardSelectorGroupBuilder();
    }

    @Override
    public SelectorBuilder selector() {
        return new StandardSelectorBuilder();
    }

    @Override
    public DeclarationBuilder declaration() {
        return new StandardDeclarationBuilder();
    }
}
