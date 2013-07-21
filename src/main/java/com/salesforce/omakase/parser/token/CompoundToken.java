/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

import com.google.common.base.CharMatcher;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class CompoundToken implements Token {
    private final String description;
    private final CharMatcher matcher;

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
        return matcher.matches(c);
    }

    @Override
    public Token or(Token other) {
        return new CompoundToken(this, other);
    }
}
