/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

/**
 * An operator, or separator, between {@link Term}s in a {@link TermList}.
 * 
 * @author nmcwilliams
 */
public enum TermOperator implements TermListMember {
    /** comma separator */
    COMMA(','),
    /** slash separator */
    SLASH('/'),
    /** white space separator */
    SINGLE_SPACE(' ');

    private final char symbol;

    TermOperator(char symbol) {
        this.symbol = symbol;
    }

    /**
     * Gets the symbol representing this separator.
     * 
     * @return The symbol.
     */
    public char symbol() {
        return symbol;
    }
}
