/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

import static com.google.common.base.CharMatcher.inRange;
import static com.google.common.base.CharMatcher.is;

import com.google.common.base.CharMatcher;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public enum Tokens implements Token {
    OPEN_BRACKET(is('{'), "opening bracket '{'"),
    CLOSE_BRACKET(is('}'), "closing bracket '}'"),
    SEMICOLON(is(';'), ";"),
    COLON(is(':'), ":"),
    ALPHA(inRange('a', 'z').or(inRange('A', 'Z')), "alpha character [a-zA-Z]"),
    STAR(is('*'), "universal selector"),
    HYPHEN(is('-'), "-"),
    AT_RULE(is('@'), "@")

    ;

    private final CharMatcher matcher;
    private final String description;

    Tokens(CharMatcher matcher, String description) {
        this.matcher = matcher;
        this.description = description;
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
