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
    // ordered by likelihood of occurrence

    /** descendant combinator */
    DESCENDANT(Tokens.NEVER_MATCH),

    /** child combinator */
    CHILD(Tokens.GREATER_THAN),

    /** adjacent sibling combinator */
    ADJACENT_SIBLING(Tokens.PLUS),

    /** general sibling combinator */
    GENERAL_SIBLING(Tokens.TILDE);

    private final Token token;

    CombinatorType(Token token) {
        this.token = token;
    }

    @Override
    public Token token() {
        return token;
    }
}
