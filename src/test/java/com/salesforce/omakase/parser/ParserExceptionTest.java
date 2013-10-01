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

package com.salesforce.omakase.parser;

import com.salesforce.omakase.test.util.TemplatesHelper;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link ParserException}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ParserExceptionTest {
    @Test
    public void exceptionMessage() {
        Source source = new Source(TemplatesHelper.GENERIC_CSS_SOURCE);
        source.forward(32);

        String msg = new ParserException(source, "test exception").getMessage();
        assertThat(msg).isEqualTo("Omakase CSS Parser - test exception:\n" +
            "at line 5, column 4 in source\n" +
            "'.test {\n" +
            "  color: #16ff2b;\n" +
            "}\n" +
            "\n" +
            "#te\u00BBst2 {\n" +
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
            "}'");
    }

    @Test
    public void exceptionMessageForLongStream() {
        Source source = new Source(TemplatesHelper.longSource());
        source.forward(4003);
        String msg = new ParserException(source, "test exception").getMessage();
        assertThat(msg).isEqualTo("Omakase CSS Parser - test exception:\n" +
            "at line 353, column 18 in source\n" +
            "'(...snipped...)x;\n" +
            "}\n" +
            "\n" +
            "a:link {\n" +
            "  color: blue;\n" +
            "  text-decoration: none;\n" +
            "}\n" +
            "\n" +
            "a:hover, a:focus »{\n" +
            "  color: red;\n" +
            "  text-decoration: red;\n" +
            "}\n" +
            "\n" +
            ".test {\n" +
            "  color: #16ff2b;\n" +
            "}\n" +
            "\n" +
            "#t(...snipped...)'");
    }

    @Test
    public void exceptionMessageForNestedStream() {
        Source source = new Source(TemplatesHelper.GENERIC_CSS_SOURCE, 22, 3);
        source.forward(32);

        String msg = new ParserException(source, "test exception").getMessage();
        assertThat(msg).isEqualTo("Omakase CSS Parser - test exception:\n" +
            "at line 5, column 4 (starting from line 22, column 3 in original source) in substring of original source\n" +
            "'.test {\n" +
            "  color: #16ff2b;\n" +
            "}\n" +
            "\n" +
            "#te\u00BBst2 {\n" +
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
            "}'");
    }

    @Test
    public void exceptionMessageWithLineAndColumnOnly() {
        String msg = new ParserException(5, 12, "test exception").getMessage();
        assertThat(msg).isEqualTo("Omakase CSS Parser - test exception:\n" +
            "at line 5, column 12.");
    }
}
