/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

import static com.google.common.base.CharMatcher.*;

import com.google.common.base.CharMatcher;

/**
 * List of {@link Token}s.
 * 
 * @author nmcwilliams
 */
public enum Tokens implements Token {
    /** upper or lower case alpha character */
    ALPHA(inRange('a', 'z').or(inRange('A', 'Z')), "alpha character [a-zA-Z]"),

    /** numerical digit */
    DIGIT(inRange('0', '9'), "numerical digit [0-9]"),

    /** dot, period, full-stop, etc... */
    DOT(is('.'), "."),

    /** hyphen or minus */
    HYPHEN(is('-'), "-"),

    /** a semicolon */
    SEMICOLON(is(';'), ";"),

    /** a regular colon */
    COLON(is(':'), ":"),

    /** comma */
    COMMA(is(','), ","),

    /** open bracket */
    OPEN_BRACKET(is('{'), "opening bracket '{'"),

    /** closing bracket */
    CLOSE_BRACKET(is('}'), "closing bracket '}'"),

    /** opening parenthesis */
    OPEN_PAREN(is('('), "("),

    /** closing parenthesis */
    CLOSE_PAREN(is(')'), ")"),

    /** asterisk */
    STAR(is('*'), "universal selector"),

    /** hash mark */
    HASH(is('#'), "#"),

    /** at symbol */
    AT_RULE(is('@'), "@"),

    /** plus character, usually for the combinator symbol */
    PLUS(is('+'), "+"),

    /** tilde character, usually for the combinator symbol */
    TILDE(is('~'), "~"),

    /** greater than character, usually for the combinator symbol */
    GREATER_THAN(is('>'), ">"),

    /** forward slash */
    FORWARD_SLASH(is('/'), "/"),

    /** double quote */
    DOUBLE_QUOTE(is('"'), "double quote"),

    /** single quote */
    SINGLE_QUOTE(is('\''), "single quote"),

    /** single space character */
    SINGLE_SPACE(is(' '), "single space character"),

    /** newline character */
    NEWLINE(is('\n'), "newline"),

    /** CSS escape character */
    ESCAPE(is('\\'), "CSS escape character"),

    /** whitespace as defined by the CSS spec */
    WHITESPACE(anyOf("\u0020\t\r\n\f"), "whitespace"),

    /** negative or positive sign */
    SIGN(anyOf("+-"), "numerical sign (- or +)"),

    /** color in hex format */
    HEX_COLOR(inRange('a', 'f').or(inRange('0', '9')).or(inRange('A', 'F')), "hex color [a-fA-F0-9]{3,6}"),

    /** first allowed character in a css ident/name (ordered based on likelihood of occurrence) */
    NMSTART(inRange('a', 'z').or(is('-')).or(inRange('A', 'Z').or(is('_'))), "valid first identifier character (no digits)"),

    /** subsequent allowed characters in a css ident/name (ordered based on likelihood of occurrence) */
    NMCHAR(inRange('a', 'z').or(is('-')).or(inRange('A', 'Z')).or(is('_')).or(inRange('0', '9')),
            "valid identifier character")

    ;

    private final CharMatcher matcher;
    private final String description;

    Tokens(CharMatcher matcher, String description) {
        this.matcher = matcher.precomputed();
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
