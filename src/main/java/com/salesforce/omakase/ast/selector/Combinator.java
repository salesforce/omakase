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
    public enum Type {
        descendant(' '),
        child('>'),
        adjacentSibling('+'),
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

    Type getType();
}
