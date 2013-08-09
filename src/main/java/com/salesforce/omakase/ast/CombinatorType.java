/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public enum CombinatorType {
    /** TODO */
    DESCENDENT(' '),
    /** TODO */
    CHILD('>'),
    /** TODO */
    ADJACENT('+'),
    /** TODO */
    GENERAL('~');

    private final char symbol;

    CombinatorType(char symbol) {
        this.symbol = symbol;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public char symbol() {
        return symbol;
    }
}
