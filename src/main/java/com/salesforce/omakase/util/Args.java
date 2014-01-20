package com.salesforce.omakase.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.declaration.RawFunction;

import java.util.List;

/**
 * Utilities for dealing with arguments, particular from {@link RawFunction} instances.
 *
 * @author nmcwilliams
 */
public final class Args {
    private Args() {}

    /**
     * TESTME
     * <p/>
     * Gets the list of comma-separated arguments in the given string.
     * <p/>
     * This will handle if the given string is encased in parenthesis, e.g., <code>(arg1, arg2)</code>.
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
     * TESTME
     * <p/>
     * Gets the list of comma-separated arguments in the given string. This should be used when you are only iterating over the
     * args, i.e., not accessing by index.
     * <p/>
     * This will handle if the given string is encased in parenthesis, e.g., <code>(arg1, arg2)</code>.
     *
     * @param raw
     *     Get the args from this string.
     *
     * @return An iterable containing the individual (trimmed) arguments).
     */
    public static Iterable<String> iterate(String raw) {
        return Splitter.on(',').trimResults().omitEmptyStrings().split(trimParens(raw.trim()));
    }

    /**
     * TESTME
     * <p/>
     * Removes the opening and closing parens, only if the first character is a '(' and last character is a ')' (whitespace is
     * trimmed before doing this check). If parens are trimmed then whitespace inside of the parens is trimmed as well.
     *
     * @param raw
     *     Trim opening and closing parens from this string.
     *
     * @return The string with the opening and closing parens trimmed, or the string unchanged if it is not encased in parens.
     */
    public static String trimParens(String raw) {
        String trimmed = raw.trim();
        if (trimmed.charAt(0) == '(' && trimmed.charAt(trimmed.length() - 1) == ')') {
            return trimmed.substring(1, trimmed.length() - 1).trim();
        }
        return raw;
    }

    /**
     * TESTME
     * <p/>
     * Strips matching, encasing quotes (" or ') from the given string.
     * <p/>
     * Note that this does not support quote escaping, and it will only strip the quotes if the opening quote is not closed before
     * the end of the string. For example, this will be trimmed:
     * <pre>
     *     "abc def"
     * </pre>
     * <p/>
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
     * TESTME
     * <p/>
     * Extracts the args inside of a function literal.
     * <p/>
     * For example given the following:
     * <pre>
     *     customFunction(arg1, arg2)
     * </pre>
     * <p/>
     * This will return:
     * <pre>
     *     arg1, arg2
     * </pre>
     *
     * @param raw
     *     Extract the args from this function literal.
     *
     * @return The extracted args.
     */
    public static String extract(String raw) {
        return raw.substring(raw.indexOf('(') + 1, raw.lastIndexOf(')'));
    }
}
