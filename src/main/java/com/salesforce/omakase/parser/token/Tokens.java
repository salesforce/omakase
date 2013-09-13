/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.parser.token;

import com.google.common.base.CharMatcher;
import com.google.common.base.Predicates;

import static com.google.common.base.CharMatcher.*;

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

    /** hyphen/minus/dash */
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
    OPEN_PAREN(is('('), "opening parenthesis '('"),

    /** closing parenthesis */
    CLOSE_PAREN(is(')'), "closing parenthesis ')'"),

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

    /** percentage symbol */
    PERCENTAGE(is('%'), "%"),

    /** double quote */
    DOUBLE_QUOTE(is('"'), "\" (double quote)"),

    /** single quote */
    SINGLE_QUOTE(is('\''), "' (single quote)"),

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
        "valid identifier character"),

    /** hyphen or digit */
    HYPHEN_OR_DIGIT(is('-').or(inRange('0', '9')), "hyphen or digit"),

    /** a token that never matches */
    NEVER_MATCH(CharMatcher.forPredicate(Predicates.alwaysFalse()), "a token that never matches");

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
        return c != null && matcher.matches(c);
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
