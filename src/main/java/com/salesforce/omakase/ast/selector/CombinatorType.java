/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.TokenEnum;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Enum of the types of CSS combinators.
 * 
 * @author nmcwilliams
 */
public enum CombinatorType implements TokenEnum<CombinatorType> {
    /** child combinator */
    CHILD(Tokens.GREATER_THAN, '>'),

    /** adjacent sibling combinator */
    ADJACENT_SIBLING(Tokens.PLUS, '+'),

    /** general sibling combinator */
    GENERAL_SIBLING(Tokens.TILDE, '~'),

    /** descendant combinator (never match because this is specially parsed) */
    DESCENDANT(Tokens.NEVER_MATCH, ' ');

    private final Token token;
    private final char symbol;

    CombinatorType(Token token, char symbol) {
        this.token = token;
        this.symbol = symbol;
    }

    @Override
    public Token token() {
        return token;
    }

    /**
     * TODO this is for output?
     * 
     * @return TODO
     */
    public char symbol() {
        return symbol;
    }
}
