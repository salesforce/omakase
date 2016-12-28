/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.parser.token;

import com.google.common.base.CharMatcher;
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

    /** question mark */
    QUESTION('?', "?"),

    /** u (unicode-range delimiter) */
    U(anyOf("Uu"), "u (case-insensitive)"),

    /** double quote */
    DOUBLE_QUOTE('"', "\" (double quote)"),

    /** single quote */
    SINGLE_QUOTE('\'', "' (single quote)"),

    /** pipe character */
    PIPE('|', "| (pipe)"),

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

    /** hexidecimal number */
    HEXIDECIMAL(inRange('0', '9').or(inRange('a', 'f').or(inRange('A', 'F'))), "hexidecimal number [a-fA-F0-9]"),

    /** first allowed character in a css ident/name (ordered based on likelihood of occurrence) */
    NMSTART(inRange('a', 'z').or(inRange('A', 'Z').or(is('_'))), "valid first identifier character (no digits)"),

    /** subsequent allowed characters in a css ident/name (ordered based on likelihood of occurrence) */
    NMCHAR(inRange('a', 'z').or(is('-')).or(inRange('A', 'Z')).or(is('_')).or(inRange('0', '9')),
        "valid identifier character"),

    /** hyphen or digit */
    HYPHEN_OR_DIGIT(is('-').or(inRange('0', '9')), "hyphen or digit"),

    /** a token that never matches */
    NEVER_MATCH(forPredicate(c -> false), "a token that never matches");

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
