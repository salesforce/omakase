/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

import static com.google.common.base.CharMatcher.inRange;
import static com.google.common.base.CharMatcher.is;

import com.google.common.base.CharMatcher;

/**
 * List of {@link Token}s.
 * 
 * @author nmcwilliams
 */
public enum Tokens implements Token {
    /** open bracket */
    OPEN_BRACKET(is('{'), "opening bracket '{'"),

    /** closing bracket */
    CLOSE_BRACKET(is('}'), "closing bracket '}'"),

    /** a semicolon */
    SEMICOLON(is(';'), ";"),

    /** a regular colon */
    COLON(is(':'), ":"),

    /** comma */
    COMMA(is(','), ","),

    /** upper or lower case alpha character */
    ALPHA(inRange('a', 'z').or(inRange('A', 'Z')), "alpha character [a-zA-Z]"),

    /** asterisk */
    STAR(is('*'), "universal selector"),

    /** hash mark */
    HASH(is('#'), "#"),

    /** dot, period, full-stop, etc... */
    DOT(is('.'), "."),

    /** hyphen */
    HYPHEN(is('-'), "-"),

    /** at symbol */
    AT_RULE(is('@'), "@"),

    /** newline character */
    NEWLINE(is('\n'), "newline"),

    /** double quote */
    DOUBLE_QUOTE(is('"'), "double quote"),

    /** single quote */
    SINGLE_QUOTE(is('\''), "single quote"),

    /** single space character */
    SINGLE_SPACE(is(' '), "single space character"),

    /** greater than character, usually for the combinator symbol */
    GREATER_THAN(is('>'), ">"),

    /** plus character, usually for the combinator symbol */
    PLUS(is('+'), "+"),

    /** tilde character, usually for the combinator symbol */
    TILDE(is('~'), "~"),

    /** first allowed character in a css ident/name */
    NMSTART(is('_').or(inRange('a', 'z')).or(inRange('A', 'Z')), "first character of an identifier"),

    /** subsequent allowed characters in a css ident/name */
    NMCHAR(is('_').or(is('-')).or(inRange('a', 'z')).or(inRange('A', 'Z').or(inRange('0', '9'))), "identifier character")

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
    public boolean matches(Character c) {
        return c == null ? false : matcher.matches(c);
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
