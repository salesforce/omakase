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

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
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
    public void exceptionWithSource() {
        Source source = new Source(TemplatesHelper.GENERIC_CSS_SOURCE);
        source.forward(32);

        String msg = new ParserException(source, "test exception").getMessage();
        assertThat(msg).isEqualTo("test exception:\n" +
            "at line 5, column 4 in\n" +
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
    public void exceptionForLongSource() {
        Source source = new Source(TemplatesHelper.longSource());
        source.forward(4003);
        String msg = new ParserException(source, "test exception").getMessage();
        assertThat(msg).isEqualTo("test exception:\n" +
            "at line 353, column 18 in\n" +
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
    public void exceptionForNestedSource() {
        Source source = new Source(TemplatesHelper.GENERIC_CSS_SOURCE, 22, 3);
        source.forward(32);

        String msg = new ParserException(source, "test exception").getMessage();
        assertThat(msg).isEqualTo("test exception:\n" +
            "at line 26, column 4 near\n" +
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
    public void exceptionForSource() {
        Syntax syntax = new Selector(new RawSyntax(5, 2, "#id"), new MasterRefiner());

        String msg = new ParserException(syntax, "test exception").getMessage();
        assertThat(msg).isEqualTo("Omakase CSS Parser - test exception:\n" +
            "at line 5, column 2, caused by\n" +
            "Selector {\n" +
            "  line: 5\n" +
            "  col: 5\n" +
            "  rawContent: RawSyntax{line=5, col=5, content=#id}\n" +
            "}");
    }

    @Test
    public void exceptionForThrowable() {
        ParserException e = new ParserException(new RuntimeException("test"));
        assertThat(e.getCause()).isInstanceOf(RuntimeException.class);
    }
}
