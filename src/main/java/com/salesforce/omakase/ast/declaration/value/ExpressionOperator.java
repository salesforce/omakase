/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public enum ExpressionOperator implements ExpressionMember {
    /** TODO */
    COMMA(','),
    /** TODO */
    SLASH('/'),
    /** TODO */
    SINGLE_SPACE(' ');

    private final char symbol;

    ExpressionOperator(char symbol) {
        this.symbol = symbol;
    }

    /**
     * TODO
     * 
     * @return TODO
     */
    public char symbol() {
        return symbol;
    }
}
