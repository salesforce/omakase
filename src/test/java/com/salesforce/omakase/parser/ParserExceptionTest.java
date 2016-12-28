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

package com.salesforce.omakase.parser;

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.Selector;
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
        Syntax syntax = new Selector(new RawSyntax(5, 2, "#id"));

        String msg = new ParserException(syntax, "test exception").getMessage();
        assertThat(msg).isEqualTo("test exception:\n" +
            "at line 5, column 2, caused by\n" +
            "#id (selector)");
    }

    @Test
    public void exceptionForThrowable() {
        ParserException e = new ParserException(new RuntimeException("test"));
        assertThat(e.getCause()).isInstanceOf(RuntimeException.class);
    }
}
