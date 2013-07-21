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
    /** TODO */
    OPEN_BRACKET(is('{'), "opening bracket '{'"),
    /** TODO */
    CLOSE_BRACKET(is('}'), "closing bracket '}'"),
    /** TODO */
    SEMICOLON(is(';'), ";"),
    /** TODO */
    COLON(is(':'), ":"),
    /** TODO */
    ALPHA(inRange('a', 'z').or(inRange('A', 'Z')), "alpha character [a-zA-Z]"),
    /** TODO */
    STAR(is('*'), "universal selector"),
    /** TODO */
    HASH(is('#'), "#"),
    /** TODO */
    DOT(is('.'), "."),
    /** TODO */
    HYPHEN(is('-'), "-"),
    /** TODO */
    AT_RULE(is('@'), "@"),
    /** TODO */
    NEWLINE(is('\n'), "newline"),
    /** TODO */
    DOUBLE_QUOTE(is('"'), "double quote"),
    /** TODO */
    SINGLE_QUOTE(is('\''), "single quote")

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
