/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static com.salesforce.omakase.parser.token.Tokens.*;

import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.TokenFactory;

/**
 * A {@link TokenFactory} for retrieving standard {@link Token} objects. Mainly using by {@link Parser}s.
 * 
 * @author nmcwilliams
 */
public class StandardTokenFactory implements TokenFactory {
    private static final Token PROPERTY_START = ALPHA.or(HYPHEN);
    private static final Token DECLARATION_END = SEMICOLON.or(CLOSE_BRACKET);
    private static final TokenFactory instance = new StandardTokenFactory();

    /** Only here to allow for subclassing. Clients should use {@link #instance()} instead. */
    protected StandardTokenFactory() {}

    /**
     * Gets the cached factory instance.
     * 
     * @return The cached instance.
     */
    public static TokenFactory instance() {
        return instance;
    }

    @Override
    public Token declarationBegin() {
        return PROPERTY_START;
    }

    @Override
    public Token declarationEnd() {
        return DECLARATION_END;
    }

}
