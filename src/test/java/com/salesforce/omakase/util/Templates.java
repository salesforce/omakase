/**
 * ADD LICENSE
 */
package com.salesforce.omakase.util;

/**
 * Common CSS source template strings for unit testing.
 * 
 * @author nmcwilliams
 */
public final class Templates {
    private static final String TEMPLATE_SELECTOR = "%s { color: red; }";

    /**
     * Gets a basic rule with the given selector.
     * 
     * @param selector
     *            The full selector string.
     * @return The CSS source code.
     */
    public static String fillSelector(String selector) {
        return String.format(TEMPLATE_SELECTOR, selector);
    }

    /**
     * Combines a CSS source with an expected result (e.g., parsing result) to be used in unit test verification.
     * 
     * @param <T>
     *            Type of the expected result.
     * @param source
     *            The complete CSS source.
     * @param expected
     *            The expected result.
     * @return A new {@link SourceWithExpectedResult} instance.
     */
    public static <T> SourceWithExpectedResult<T> withExpectedResult(String source, T expected) {
        SourceWithExpectedResult<T> ts = new SourceWithExpectedResult<>();
        ts.source = source;
        ts.expected = expected;
        return ts;
    }

    /**
     * A CSS source combined with a generic result.
     * 
     * @param <T>
     *            The type of the result.
     */
    public static final class SourceWithExpectedResult<T> {
        /** The CSS source */
        public String source;
        /** The expected result */
        public T expected;
    }
}
