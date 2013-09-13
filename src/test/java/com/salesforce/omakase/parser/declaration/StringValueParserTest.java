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

package com.salesforce.omakase.parser.declaration;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.declaration.value.StringValue;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.Templates.SourceWithExpectedResult;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link StringValueParser}.
 * <p/>
 * XXX the spec allows for escaped newlines in strings as well, see http://www.w3.org/TR/css3-values/#strings
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "SpellCheckingInspection"})
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
            StringValue value = result.broadcaster.findOnly(StringValue.class).get();
            assertThat(value.content())
                .describedAs(result.stream.toString())
                .isEqualTo(result.expected);
        }
    }

    @Test
    public void errorsOnUnclosedDoubleQuote() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("\"afafafafa");
    }

    @Test
    public void errorsOnUnclosedDoubleQuoteEscaped() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("\"afafafafa\\\"");
    }

    @Test
    public void errorsOnUnclosedDoubleQuoteSingleQuote() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("\"afafafafa'");
    }

    @Test
    public void errorsOnUnclosedDoubleQuoteThreeEscapes() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("\"afafafafa\\\"afafafafa\\\"afafa");
    }

    @Test
    public void errorsOnUnclosedSingleQuote() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("'afafafafaf");
    }

    @Test
    public void errorsOnUnclosedSingleQuoteEscaped() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("'afafafafaf\\'");
    }

    @Test
    public void errorsOnUnclosedSingleQuoteSingleQuote() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("'afafafafa\"");
    }

    @Test
    public void errorsOnUnclosedSingleQuoteThreeEscapes() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("'asfasfs\\'asfasfas\\'sfsf");
    }
}
