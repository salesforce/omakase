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

package com.salesforce.omakase.test.util;

/**
 * Common CSS source template strings for unit testing.
 *
 * @author nmcwilliams
 */
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
        SourceWithExpectedResult<T> ts = new SourceWithExpectedResult<>();
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
