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
     * This will also handle if the given string includes wrapping parenthesis, e.g., (arg1, arg2).
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
     * This will also handle if the given string includes wrapping parenthesis, e.g., (arg1, arg2).
     *
     * @param raw
     *     Get the args from this string.
     *
     * @return An iterable containing the individual (trimmed) arguments).
     */
    public static Iterable<String> iterate(String raw) {
        String args = raw.trim();

        // check if we need to strip parens
        if (args.charAt(0) == '(') {
            args = raw.substring(args.indexOf('(') + 1, args.lastIndexOf(')')).trim();
        }

        return Splitter.on(',').trimResults().omitEmptyStrings().split(args);
    }
}
