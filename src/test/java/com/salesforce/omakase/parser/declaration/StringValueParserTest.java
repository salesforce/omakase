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

package com.salesforce.omakase.parser.declaration;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.declaration.StringValue;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;

/**
 * Unit tests for {@link StringValueParser}.
 * <p>
 * future: the spec allows for escaped newlines in strings as well, see http://www.w3.org/TR/css3-values/#strings
 *
 * @author nmcwilliams
 */
public class StringValueParserTest extends AbstractParserTest<StringValueParser> {

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "abc'",
            "afa",
            "afafa\"",
            "123af\"afaf\"faf",
            "afafafaf'ssgs'sgsgsg"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "\"agg\"",
            "\"af\\\"afa\"",
            "'sfsfs'",
            "'sfsfsf\\'sfsfsf'",
            "\"121313\"",
            "\"this is a 'string'.\"",
            "\"this is a \\\"string\\\".\"",
            "'this is a \"string\".'",
            "'this is a \\'string\\'.'"
        );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("\"this is a 'string'.\", afafaf", 21),
            withExpectedResult("\"this is a \\\"string\\\".\", afa'f\"ad", 23),
            withExpectedResult("'this is a \"string\".' 'afafafa'", 21),
            withExpectedResult("'this is a \\'string\\'.'\"afa\"", 23));
    }

    @Override
    public String validSourceForPositionTesting() {
        return Iterables.get(validSources(), 0);
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return false;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("\"this is a 'string'.\"", "this is a 'string'."),
            withExpectedResult("\"this is a \\\"string\\\".\"", "this is a \\\"string\\\"."),
            withExpectedResult("'this is a \"string\".'", "this is a \"string\"."),
            withExpectedResult("'this is a \\'string\\'.'", "this is a \\'string\\'."));

        for (ParseResult<String> result : results) {
            StringValue value = expectOnly(result.broadcaster, StringValue.class);
            assertThat(value.content())
                .describedAs(result.source.toString())
                .isEqualTo(result.expected);
        }
    }

    @Test
    public void errorsOnUnclosedDoubleQuote() {
        ParserException thrown = assertThrows(ParserException.class, () -> parse("\"afafafafa"));
        assertTrue(thrown.getMessage().contains("Expected to find closing"));
    }

    @Test
    public void errorsOnUnclosedDoubleQuoteEscaped() {
        ParserException thrown = assertThrows(ParserException.class, () -> parse("\"afafafafa\\\""));
        assertTrue(thrown.getMessage().contains("Expected to find closing"));
    }

    @Test
    public void errorsOnUnclosedDoubleQuoteSingleQuote() {
        ParserException thrown = assertThrows(ParserException.class, () -> parse("\"afafafafa'"));
        assertTrue(thrown.getMessage().contains("Expected to find closing"));
    }

    @Test
    public void errorsOnUnclosedDoubleQuoteThreeEscapes() {
        ParserException thrown = assertThrows(ParserException.class, () -> parse("\"afafafafa\\\\\\\"afafafafa\\\\\\\"afafa"));
        assertTrue(thrown.getMessage().contains("Expected to find closing"));
    }

    @Test
    public void errorsOnUnclosedSingleQuote() {
        ParserException thrown = assertThrows(ParserException.class, () -> parse("'afafafafaf"));
        assertTrue(thrown.getMessage().contains("Expected to find closing"));
    }

    @Test
    public void errorsOnUnclosedSingleQuoteEscaped() {
        ParserException thrown = assertThrows(ParserException.class, () -> parse("'afafafafaf\\\\'"));
        assertTrue(thrown.getMessage().contains("Expected to find closing"));
    }

    @Test
    public void errorsOnUnclosedSingleQuoteSingleQuote() {
        ParserException thrown = assertThrows(ParserException.class, () -> parse("'afafafafa\\\""));
        assertTrue(thrown.getMessage().contains("Expected to find closing"));
    }

    @Test
    public void errorsOnUnclosedSingleQuoteThreeEscapes() {
        ParserException thrown = assertThrows(ParserException.class, () -> parse("'asfasfs\\\\'asfasfas\\\\'sfsf"));
        assertTrue(thrown.getMessage().contains("Expected to find closing"));
    }
}
