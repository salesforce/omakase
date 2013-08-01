/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import com.salesforce.omakase.ast.builder.*;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StandardSyntaxFactory implements SyntaxFactory {
    private final static StandardSyntaxFactory instance = new StandardSyntaxFactory();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static SyntaxFactory instance() {
        return instance;
    }

    /** TODO */
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
