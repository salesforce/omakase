/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.parser.token.StandardTokenFactory;
import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.TokenFactory;

/**
 * Base class for {@link Parser}s.
 *
 * @author nmcwilliams
 */
public abstract class AbstractParser implements Parser {
    private final TokenFactory tokenFactory;

    /** Creates a new {@link AbstractParser} instance with using a standard {@link TokenFactory}. */
    public AbstractParser() {
        this(StandardTokenFactory.instance());
    }

    /**
     * Creates a new {@link AbstractParser} instance using the given {@link TokenFactory}.
     *
     * @param tokenFactory
     *     Use this factory for retrieving {@link Token} delimiters.
     */
    public AbstractParser(TokenFactory tokenFactory) {
        this.tokenFactory = tokenFactory;
    }

    /**
     * Utility method to create a {@link CombinationParser} comprised of this and the given {@link Parser}.
     *
     * @param other
     *     The {@link Parser} to use.
     *
     * @return A new {@link CombinationParser} instance.
     */
    @Override
    public Parser or(Parser other) {
        return new CombinationParser(this, other);
    }

    /**
     * Gets the {@link TokenFactory} to use for various {@link Token} delimiters.
     *
     * @return The factory.
     */
    protected TokenFactory tokenFactory() {
        return tokenFactory;
    }
}
