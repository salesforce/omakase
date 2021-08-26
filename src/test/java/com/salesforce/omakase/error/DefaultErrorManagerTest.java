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

package com.salesforce.omakase.error;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.emitter.SubscriptionException;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;

/**
 * Unit tests for {@link DefaultErrorManager}.
 *
 * @author nmcwilliams
 */
public class DefaultErrorManagerTest {

    @Test
    public void reportParserExceptionOneError() {
        DefaultErrorManager em = new DefaultErrorManager().rethrow(false);
        Source source = new Source("{ ...");
        source.forward(5);
        em.report(new ParserException(source, "Expected to find closing brace '{'"));

        String expected = "Omakase CSS Parser - Errors\n" +
            "----------------------------\n" +
            "Expected to find closing brace '{':\n" +
            "at line 1, column 6 in\n" +
            "'{ ...»'\n";
        assertThat(em.summarize()).isEqualTo(expected);
    }

    @Test
    public void reportParserExceptionOneErrorWithSourceName() {
        DefaultErrorManager em = new DefaultErrorManager("styles.css").rethrow(false);
        Source source = new Source("{ ...");
        source.forward(5);
        em.report(new ParserException(source, "Expected to find closing brace '{'"));

        String expected = "Omakase CSS Parser - Errors\n" +
            "----------------------------\n" +
            "styles.css:\n" +
            "Expected to find closing brace '{':\n" +
            "at line 1, column 6 in\n" +
            "'{ ...»'\n";
        assertThat(em.summarize()).isEqualTo(expected);
    }

    @Test
    public void reportParserExceptionTwoErrors() {
        DefaultErrorManager em = new DefaultErrorManager().rethrow(false);

        Source source1 = new Source("{ ...");
        source1.forward(5);
        em.report(new ParserException(source1, "Expected to find closing brace '{'"));

        Source source2 = new Source("url(foo.png ");
        source2.forward(11);
        em.report(new ParserException(source1, "Expected to find closing parenthesis ')'"));

        String expected = "Omakase CSS Parser - Errors\n" +
            "----------------------------\n" +
            "Expected to find closing brace '{':\n" +
            "at line 1, column 6 in\n" +
            "'{ ...»'\n" +
            "\n" +
            "Expected to find closing parenthesis ')':\n" +
            "at line 1, column 6 in\n" +
            "'{ ...»'\n";
        assertThat(em.summarize()).isEqualTo(expected);
    }

    @Test
    public void rethrowsParserException() {
        DefaultErrorManager em = new DefaultErrorManager().rethrow(true);
        Source source = new Source("{ ...");
        source.forward(5);
        assertThrows(ParserException.class, () -> em.report(new ParserException(source, "Expected to find closing brace '{'")));
    }

    @Test
    public void reportsOneSubscriptionExNoRethrow() {
        DefaultErrorManager em = new DefaultErrorManager().rethrow(false);
        em.report(new SubscriptionException("Exception thrown from a CSS Parser plugin method", new NullPointerException()));

        String expected = "Omakase CSS Parser - Plugin Errors\n" +
            "-----------------------------------\n" +
            "Exception thrown from a CSS Parser plugin method:\n" +
            "(find the cause below)\n" +
            "java.lang.NullPointerException\n";
        assertThat(em.summarize()).isEqualTo(expected);
    }

    @Test
    public void rethrowsSubscriptionException() {
        DefaultErrorManager em = new DefaultErrorManager();
        assertThrows(SubscriptionException.class, () -> em.report(new SubscriptionException("Exception thrown from a CSS Parser plugin method", new NullPointerException())));

    }

    @Test
    public void reportWithCauseAndMessageFatal() {
        ClassSelector cs = new ClassSelector(5, 2, "myClass");

        DefaultErrorManager em = new DefaultErrorManager();
        em.report(ErrorLevel.FATAL, cs, "invalid class name");

        String expected = "Omakase CSS Parser - Errors\n" +
            "----------------------------\n" +
            "invalid class name:\n" +
            "at line 5, column 2, caused by\n" +
            ".myClass (class-selector)\n";
        assertThat(em.summarize()).isEqualTo(expected);
    }

    @Test
    public void reportWithCauseAndMessageWarning() {
        ClassSelector cs = new ClassSelector(5, 2, "myClass");

        DefaultErrorManager em = new DefaultErrorManager();
        em.report(ErrorLevel.WARNING, cs, "invalid class name");

        String expected = "Omakase CSS Parser - Warnings\n" +
            "------------------------------\n" +
            "invalid class name:\n" +
            "at line 5, column 2, caused by\n" +
            ".myClass (class-selector)\n";
        assertThat(em.summarize()).isEqualTo(expected);
    }

    @Test
    public void reportWithCauseAndMessageTwoWarnings() {
        ClassSelector cs = new ClassSelector(5, 2, "myClass");
        ClassSelector cs2 = new ClassSelector(12, 6, "yourClass");

        DefaultErrorManager em = new DefaultErrorManager();
        em.report(ErrorLevel.WARNING, cs, "invalid class name");
        em.report(ErrorLevel.WARNING, cs2, "invalid class name");

        String expected = "Omakase CSS Parser - Warnings\n" +
            "------------------------------\n" +
            "invalid class name:\n" +
            "at line 5, column 2, caused by\n" +
            ".myClass (class-selector)\n" +
            "\n" +
            "invalid class name:\n" +
            "at line 12, column 6, caused by\n" +
            ".yourClass (class-selector)\n";
        assertThat(em.summarize()).isEqualTo(expected);
    }

    @Test
    public void reportWithCauseAndMessageWarningAndSourceName() {
        ClassSelector cs = new ClassSelector(5, 2, "myClass");

        DefaultErrorManager em = new DefaultErrorManager("styles.css");
        em.report(ErrorLevel.WARNING, cs, "invalid class name");

        String expected = "Omakase CSS Parser - Warnings\n" +
            "------------------------------\n" +
            "invalid class name:\n" +
            "at line 5, column 2 (styles.css), caused by\n" +
            ".myClass (class-selector)\n";
        assertThat(em.summarize()).isEqualTo(expected);
    }

    @Test
    public void reportWithCauseAndParent() {
        Selector selector = new Selector();
        ClassSelector cs = new ClassSelector(5, 2, "myClass1");
        ClassSelector cs2 = new ClassSelector(5, 2, "myClass2");
        selector.append(cs).append(cs2);

        DefaultErrorManager em = new DefaultErrorManager();
        em.report(ErrorLevel.WARNING, cs2, "invalid class name");

        String expected = "Omakase CSS Parser - Warnings\n" +
            "------------------------------\n" +
            "invalid class name:\n" +
            "at line 5, column 2, caused by\n" +
            ".myClass2 (class-selector)\n" +
            "in\n" +
            ".myClass1.myClass2 (selector)\n";
        assertThat(em.summarize()).isEqualTo(expected);
    }

    @Test
    public void reportMulti() {
        DefaultErrorManager em = new DefaultErrorManager().rethrow(false);
        Source source = new Source("{ ...");
        source.forward(5);
        em.report(new ParserException(source, "Expected to find closing brace '{'"));

        em.report(new SubscriptionException("Exception thrown from a CSS Parser plugin method", new NullPointerException()));

        ClassSelector cs = new ClassSelector(5, 2, "myClass");
        em.report(ErrorLevel.WARNING, cs, "invalid class name");

        String expected = "Omakase CSS Parser - Plugin Errors\n" +
            "-----------------------------------\n" +
            "Exception thrown from a CSS Parser plugin method:\n" +
            "(find the cause below)\n" +
            "java.lang.NullPointerException\n" +
            "\n" +
            "Omakase CSS Parser - Errors\n" +
            "----------------------------\n" +
            "Expected to find closing brace '{':\n" +
            "at line 1, column 6 in\n" +
            "'{ ...»'\n" +
            "\n" +
            "Omakase CSS Parser - Warnings\n" +
            "------------------------------\n" +
            "invalid class name:\n" +
            "at line 5, column 2, caused by\n" +
            ".myClass (class-selector)\n";
        assertThat(em.summarize()).isEqualTo(expected);
    }

    @Test
    public void reportNoWarnings() {
        DefaultErrorManager em = new DefaultErrorManager().rethrow(false).warnings(false);
        Source source = new Source("{ ...");
        source.forward(5);
        em.report(new ParserException(source, "Expected to find closing brace '{'"));

        ClassSelector cs = new ClassSelector(5, 2, "myClass");
        em.report(ErrorLevel.WARNING, cs, "invalid class name");

        String expected = "Omakase CSS Parser - Errors\n" +
            "----------------------------\n" +
            "Expected to find closing brace '{':\n" +
            "at line 1, column 6 in\n" +
            "'{ ...»'\n";
        assertThat(em.summarize()).isEqualTo(expected);
    }

    @Test
    public void hasErrorsFalse() {
        assertThat(new DefaultErrorManager().hasErrors()).isFalse();
    }

    @Test
    public void hasErrorsTrue() {
        DefaultErrorManager em = new DefaultErrorManager().rethrow(false);
        Source source = new Source("{ ...");
        em.report(new ParserException(source, "Expected to find closing brace '{'"));

        assertThat(em.hasErrors()).isTrue();
    }

    @Test
    public void hasErrorsTrueWithWarnings() {
        DefaultErrorManager em = new DefaultErrorManager().rethrow(false);
        Source source = new Source("{ ...");
        em.report(new ParserException(source, "Expected to find closing brace '{'"));

        assertThat(em.hasErrors()).isTrue();
    }

    @Test
    public void hasErrorsFalseBecauseWarningsOff() {
        DefaultErrorManager em = new DefaultErrorManager().warnings(false);
        ClassSelector cs = new ClassSelector(5, 2, "myClass");
        em.report(ErrorLevel.WARNING, cs, "invalid class name");

        assertThat(em.hasErrors()).isFalse();
    }

    @Test
    public void hasErrorsTrueBecauseOfSubscriptionEx() {
        DefaultErrorManager em = new DefaultErrorManager().rethrow(false);
        em.report(new SubscriptionException("Exception thrown from a CSS Parser plugin method", new NullPointerException()));

        assertThat(em.hasErrors()).isTrue();
    }
}
