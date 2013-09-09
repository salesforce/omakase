/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

import com.google.common.base.CharMatcher;
import com.salesforce.omakase.As;

/**
 * A combination matcher that does an OR comparison of two {@link Token}s.
 *
 * @author nmcwilliams
 */
public class CompoundToken implements Token {
    private final String description;
    private final CharMatcher matcher;

    /**
     * Constructs a new {@link CompoundToken} for doing OR character comparisons. The descriptions of each will be combined.
     *
     * @param first
     *     The first {@link Token}.
     * @param second
     *     The second {@link Token}.
     */
    public CompoundToken(Token first, Token second) {
        this.matcher = first.matcher().or(second.matcher());

        StringBuilder sb = new StringBuilder(first.description().length() + second.description().length() + 4);
        sb.append(first.description());
        sb.append(" OR ");
        sb.append(second.description());
        this.description = sb.toString();
    }

    @Override
    public CharMatcher matcher() {
        return matcher;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public boolean matches(Character c) {
        return c != null && matcher.matches(c);
    }

    @Override
    public Token or(Token other) {
        return new CompoundToken(this, other);
    }

    @Override
    public String toString() {
        return As.string(this).add("description", description).toString();
    }
}
