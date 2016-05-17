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

package com.salesforce.omakase.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.declaration.RawFunction;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Utilities for dealing with arguments, particular from {@link RawFunction} instances.
 *
 * @author nmcwilliams
 */
public final class Args {
    private static final Pattern NEWLINES = Pattern.compile("\\r|\\n");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private Args() {}

    /**
     * Gets the list of comma-separated arguments in the given string.
     * <p>
     * This will handle if the given string is encased in parenthesis, e.g., <code>(arg1, arg2)</code>. This does not distinguish
     * between commas inside of quotes or handle escaped commas.
     * <p>
     * <p>
     * For example given the following:
     * <pre>
     *     (arg1, arg2)
     * </pre>
     * <p>
     * This will return:
     * <pre>
     *     List[arg1, arg2]
     * </pre>
     * <p>
     * If only iterating over the args, use {@link #iterate(String)} instead.
     *
     * @param raw
     *     Get the args from this string.
     *
     * @return The list of individual (trimmed) arguments.
     */
    public static List<String> get(String raw) {
        return Lists.newArrayList(iterate(raw));
    }

    /**
     * Gets the list of comma-separated arguments in the given string. This should be used when you are only iterating over the
     * args, i.e., not accessing by index.
     * <p>
     * This will handle if the given string is encased in parenthesis, e.g., <code>(arg1, arg2)</code>. This does not distinguish
     * between commas inside of quotes or handle escaped commas.
     *
     * @param raw
     *     Get the args from this string.
     *
     * @return An iterable containing the individual (trimmed) arguments).
     */
    public static Iterable<String> iterate(String raw) {
        return Splitter.on(',').trimResults().omitEmptyStrings().split(trimParens(raw));
    }

    /**
     * Removes the opening and closing parens, only if the first character is a '(' and last character is a ')' (whitespace is
     * trimmed before doing this check). If parens are trimmed then whitespace inside of the parens is trimmed as well.
     * <p>
     * For example given the following:
     * <pre>
     *    (arg1, arg2 )
     * </pre>
     * <p>
     * This will return:
     * <pre>
     *     "arg1, arg2"
     * </pre>
     *
     * @param raw
     *     Trim opening and closing parens from this string.
     *
     * @return The string with the opening and closing parens trimmed, or the string unchanged if it is not encased in parens.
     */
    public static String trimParens(String raw) {
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) return raw;
        if (trimmed.charAt(0) == '(' && trimmed.charAt(trimmed.length() - 1) == ')') {
            return trimmed.substring(1, trimmed.length() - 1).trim();
        }
        return raw;
    }

    /**
     * Extracts the args inside of a function literal.
     * <p>
     * For example given the following:
     * <pre>
     *     customFunction(arg1, arg2)
     * </pre>
     * <p>
     * This will return:
     * <pre>
     *     "arg1, arg2"
     * </pre>
     *
     * @param raw
     *     Extract the args from this function literal.
     *
     * @return The extracted args.
     *
     * @throws IndexOutOfBoundsException
     *     If encasing parenthesis are not present in the given string.
     */
    public static String extract(String raw) {
        return raw.substring(raw.indexOf('(') + 1, raw.lastIndexOf(')'));
    }

    /**
     * Strips matching, encasing quotes (" or ') from the given string.
     * <p>
     * Note that this does not support quote escaping, and it will only strip the quotes if the opening quote is not closed before
     * the end of the string. For example, these will be trimmed:
     * <pre>
     *     "abc def"
     *     'abc def'
     * </pre>
     * <p>
     * however this will not:
     * <pre>
     *     "abc" + 123 + "abc"
     * </pre>
     *
     * @param raw
     *     Trim the quotes around this string.
     *
     * @return The string with the quotes trimmed, or the same string as given if it does not meet the criteria described above.
     */
    public static String trimQuotesSimple(String raw) {
        char first = raw.charAt(0);
        char last = raw.charAt(raw.length() - 1);

        if (first != last || (first != '\'' && first != '"')) return raw;

        boolean entirelyQuoted = raw.indexOf(first, 1) == raw.length() - 1;
        return entirelyQuoted ? raw.substring(1, raw.length() - 1).trim() : raw;
    }

    /**
     * Strips matching double quotes from the given string.
     * <p>
     * Note that this does not support quote escaping, and it will only strip the quotes if the opening quote is not closed before
     * the end of the string. For example, this will be trimmed:
     * <pre>
     *     "abc def"
     * </pre>
     * <p>
     * however this will not:
     * <pre>
     *     "abc" + 123 + "abc"
     * </pre>
     *
     * @param raw
     *     Trim the quotes around this string.
     *
     * @return The string with the quotes trimmed, or the same string as given if it does not meet the criteria described above.
     */
    public static String trimDoubleQuotes(String raw) {
        if (raw.charAt(0) != '"' || raw.charAt(raw.length() - 1) != '"') return raw;
        boolean entirelyQuoted = raw.indexOf('"', 1) == raw.length() - 1;
        return entirelyQuoted ? raw.substring(1, raw.length() - 1).trim() : raw;
    }

    /**
     * Removes newlines and repeating whitespace from arguments.
     *
     * @param originalArgs
     *     The original arguments.
     *
     * @return The formatted arguments.
     */
    public static String clean(String originalArgs) {
        String formatted = originalArgs;
        formatted = NEWLINES.matcher(formatted).replaceAll("");
        formatted = WHITESPACE.matcher(formatted).replaceAll(" ");
        return formatted.trim();
    }
}
