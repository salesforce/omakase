/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import java.io.IOException;

import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.TokenEnum;
import com.salesforce.omakase.parser.token.Tokens;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * TESTME An operator, or separator, between {@link Term}s in a {@link TermList}.
 * 
 * @author nmcwilliams
 */
public enum TermOperator implements TermListMember, TokenEnum<TermOperator> {
    /** comma separator */
    COMMA(Tokens.COMMA, ','),
    /** slash separator */
    SLASH(Tokens.FORWARD_SLASH, '/'),
    /** white space separator */
    SINGLE_SPACE(Tokens.SINGLE_SPACE, ' ');

    private final Token token;
    private final char symbol;

    TermOperator(Token token, char symbol) {
        this.token = token;
        this.symbol = symbol;
    }

    @Override
    public Token token() {
        return token;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(symbol);
    }
}
