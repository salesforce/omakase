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
public class CustomToken implements Token {
    private final CharMatcher matcher;
    private final String description;

    /**
     * TODO
     * 
     * @param character
     *            TODO
     * @param description
     *            TODO
     */
    public CustomToken(char character, String description) {
        matcher = CharMatcher.is(character);
        this.description = description;
    }

    @Override
    public CharMatcher matcher() {
        return matcher;
    }

    @Override
    public boolean matches(Character c) {
        return matcher.matches(c);
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Token or(Token other) {
        return new CompoundToken(this, other);
    }
}
