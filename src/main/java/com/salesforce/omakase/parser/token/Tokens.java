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
import com.salesforce.omakase.parser.Source;

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
    DOT('.', "."),

    /** hyphen/minus/dash */
    HYPHEN('-', "-"),

    /** a semicolon */
    SEMICOLON(';', ";"),

    /** a regular colon */
    COLON(':', ":"),

    /** comma */
    COMMA(',', ","),

    /** open brace { */
    OPEN_BRACE('{', "opening brace '{'"),

    /** closing brace } */
    CLOSE_BRACE('}', "closing brace '}'"),

    /** opening parenthesis ( */
    OPEN_PAREN('(', "opening parenthesis '('"),

    /** closing parenthesis ) */
    CLOSE_PAREN(')', "closing parenthesis ')'"),

    /** opening bracket [ */
    OPEN_BRACKET('[', "opening bracket '['"),

    /** closing bracket ] */
    CLOSE_BRACKET(']', "closing bracket ']'"),

    /** asterisk */
    STAR('*', "universal selector"),

    /** hash mark */
    HASH('#', "#"),

    /** at symbol */
    AT_RULE('@', "@"),

    /** plus character, usually for the combinator symbol */
    PLUS('+', "+"),

    /** tilde character, usually for the combinator symbol */
    TILDE('~', "~"),

    /** greater than character, usually for the combinator symbol */
    GREATER_THAN('>', ">"),

    /** forward slash */
    FORWARD_SLASH('/', "/"),

    /** percentage symbol */
    PERCENTAGE('%', "%"),

    /** exclamation */
    EXCLAMATION('!', "!"),

    /** double quote */
    DOUBLE_QUOTE('"', "\" (double quote)"),

    /** single quote */
    SINGLE_QUOTE('\'', "' (single quote)"),

    /** newline character */
    NEWLINE('\n', "newline"),

    /** CSS escape character */
    ESCAPE('\\', "CSS escape character"),

    /** whitespace as defined by the CSS spec (except form feed) */
    WHITESPACE(anyOf("\u0020\n\t\r"), "whitespace"),

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
    NEVER_MATCH(forPredicate(Predicates.alwaysFalse()), "a token that never matches");

    private final char singleChar;
    private final boolean isSingleChar;
    private final CharMatcher matcher;
    private final String description;

    Tokens(CharMatcher matcher, String description) {
        this.isSingleChar = false;
        this.singleChar = Source.NULL_CHAR;
        this.matcher = matcher.precomputed();
        this.description = description;
    }

    Tokens(char singleChar, String description) {
        this.isSingleChar = true;
        this.singleChar = singleChar;
        this.matcher = null;
        this.description = description;
    }

    @Override
    public boolean matches(char c) {
        if (isSingleChar) return (singleChar - c) == 0;
        return c != Source.NULL_CHAR && matcher.matches(c);
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
