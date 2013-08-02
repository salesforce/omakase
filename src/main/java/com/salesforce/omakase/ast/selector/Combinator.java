/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Combinator extends SelectorPart {
    /** TODO */
    public enum Type {
        /** TODO */
        descendant(' '),
        /** TODO */
        child('>'),
        /** TODO */
        adjacentSibling('+'),
        /** TODO */
        generalSibling('~');

        private final char symbol;

        Type(char symbol) {
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

    /**
     * TODO Description
     * 
     * @return TODO
     */
    Type getType();
}
