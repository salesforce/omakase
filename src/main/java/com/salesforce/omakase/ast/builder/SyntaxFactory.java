/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface SyntaxFactory {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    StylesheetBuilder stylesheet();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    RuleBuilder rule();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    SelectorGroupBuilder selectorGroup();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    SelectorBuilder selector();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    DeclarationBuilder declaration();
}
