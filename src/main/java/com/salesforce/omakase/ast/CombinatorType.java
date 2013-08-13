/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

/**
 * Enum of the types of CSS combinators.
 * 
 * @author nmcwilliams
 */
public enum CombinatorType {
    /** descendant combinator */
    DESCENDANT(' '),
    /** child combinator */
    CHILD('>'),
    /** adjacent sibling combinator */
    ADJACENT_SIBLING('+'),
    /** general sibling combinator */
    GENERAL_SIBLING('~');

    private final char symbol;

    CombinatorType(char symbol) {
        this.symbol = symbol;
    }

    /**
     * Gets the character representation of this combinator.
     * 
     * @return The combinator's character.
     */
    public char symbol() {
        return symbol;
    }
}
