/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public enum TermOperator implements TermMember {
    /** TODO */
    COMMA(','),
    /** TODO */
    SLASH('/'),
    /** TODO */
    SINGLE_SPACE(' ');

    private final char symbol;

    TermOperator(char symbol) {
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
