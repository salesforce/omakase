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

package com.salesforce.omakase.util;

/**
 * Common CSS source template strings for unit testing.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public final class TemplatesHelper {
    public static final String GENERIC_CSS_SOURCE = ".test {\n" +
        "  color: #16ff2b;\n" +
        "}\n" +
        "\n" +
        "#test2 {\n" +
        "  margin: 5px 10px;\n" +
        "  padding: 10px;\n" +
        "  border: 1px solid red;\n" +
        "  border-radius: 10px;\n" +
        "}\n" +
        "\n" +
        "a:link {\n" +
        "  color: blue;\n" +
        "  text-decoration: none;\n" +
        "}\n" +
        "\n" +
        "a:hover, a:focus {\n" +
        "  color: red;\n" +
        "  text-decoration: red;\n" +
        "}";

    private static final String TEMPLATE_SELECTOR = "%s { color: red; }";
    private static final String TEMPLATE_PROPERTY_VALUE = ".test { %s: %s; }";

    /** do not construct */
    private TemplatesHelper() {}

    /**
     * Gets a basic rule with the given selector.
     *
     * @param selector
     *     The full selector string.
     *
     * @return The CSS source code.
     */
    public static String fillSelector(String selector) {
        return String.format(TEMPLATE_SELECTOR, selector);
    }

    /**
     * Gets a basic rule with the given property name and value.
     *
     * @param name
     *     The property name.
     * @param value
     *     The property value.
     *
     * @return The CSS source code.
     */
    public static String fillDeclaration(String name, String value) {
        return String.format(TEMPLATE_PROPERTY_VALUE, name, value);
    }

    /**
     * Gets a version of {@link #GENERIC_CSS_SOURCE} that's a lot lot longer.
     *
     * @return The source.
     */
    public static StringBuilder longSource() {
        StringBuilder src = new StringBuilder(GENERIC_CSS_SOURCE.length() * 40);
        for (int i = 0; i < 40; i++) {
            src.append(GENERIC_CSS_SOURCE).append("\n\n");
        }

        return src;
    }

    /**
     * Combines a CSS source with an expected result (e.g., parsing result) to be used in unit test verification.
     *
     * @param <T>
     *     Type of the expected result.
     * @param source
     *     The complete CSS source.
     * @param expected
     *     The expected result.
     *
     * @return A new {@link SourceWithExpectedResult} instance.
     */
    public static <T> SourceWithExpectedResult<T> withExpectedResult(String source, T expected) {
        SourceWithExpectedResult<T> ts = new SourceWithExpectedResult<T>();
        ts.source = source;
        ts.expected = expected;
        return ts;
    }

    /**
     * A CSS source combined with a generic result.
     *
     * @param <T>
     *     The type of the result.
     */
    public static final class SourceWithExpectedResult<T> {
        /** The CSS source */
        public String source;
        /** The expected result */
        public T expected;
    }
}
