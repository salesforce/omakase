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

package com.salesforce.omakase.parser.selector;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.AttributeMatchType;
import com.salesforce.omakase.ast.selector.AttributeSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper;

/**
 * Unit tests for {@link AttributeSelectorParser}.
 *
 * @author nmcwilliams
 */
public class AttributeSelectorParserTest extends AbstractParserTest<AttributeSelectorParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "  ",
            "\n",
            "  [afaf",
            "\n [afa",
            "abc",
            "123",
            "{hello",
            "\"[although]\"",
            "a[href]"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "[href]",
            "[href=\"http://perishablepress.com\"]",
            "[href=\"http://peri]shablepress.com\"]",
            "[href=\"http://peri]s[]hablepress.com\"]",
            "[href=\"http://peri]s[]hab\\\"lepr'ess.com\"]",
            "[class]",
            "[foo=\"bar\"]",
            "[foo~=\"bar\"]",
            "[foo^=\"bar\"]",
            "[foo$=\"bar\"]",
            "[foo*=\"bar\"]",
            "[foo|=\"en\"]",
            "[foo=bar]",
            "[foo~=bar]",
            "[foo^=bar]",
            "[foo$=bar]",
            "[foo*=bar]",
            "[foo|=en]"
        );
    }

    @Override
    public List<TemplatesHelper.SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("[href]", 6),
            withExpectedResult("[href] blah", 6),
            withExpectedResult("[href]   ", 6),
            withExpectedResult("[href][class]", 6),
            withExpectedResult("[href=\"http://peri]s[]hab\\\"lepr'ess.com\"] #id", 41),
            withExpectedResult("[href] > .class", 6),
            withExpectedResult("[foo*=\"bar\"]\n[href]", 12));
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
            withExpectedResult("[class]", "class"),
            withExpectedResult("[href]", "href"),
            withExpectedResult("[href=\"http://peri]s[]hab\\\"lepr'ess.com\"]", "href"),
            withExpectedResult("[foo=\"bar\"]", "foo"),
            withExpectedResult("[foo~=\"bar\"]", "foo"),
            withExpectedResult("[foo^=\"bar\"]", "foo"),
            withExpectedResult("[foo$=\"bar\"]", "foo"),
            withExpectedResult("[foo*=\"bar\"]", "foo"),
            withExpectedResult("[foo|=\"en\"]", "foo"),
            withExpectedResult("[foo=bar]", "foo"),
            withExpectedResult("[foo~=bar]", "foo"),
            withExpectedResult("[foo^=bar]", "foo"),
            withExpectedResult("[foo$=bar]", "foo"),
            withExpectedResult("[foo*=bar]", "foo"),
            withExpectedResult("[foo|=en]", "foo")
        );

        for (ParseResult<String> result : results) {
            AttributeSelector selector = expectOnly(result.broadcaster, AttributeSelector.class);
            assertThat(selector.attribute())
                .describedAs(result.source.toString())
                .isEqualTo(result.expected);
        }
    }

    @Test
    public void matchesExpectedMatchType() {
        List<ParseResult<AttributeMatchType>> results = parseWithExpected(
            withExpectedResult("[foo=\"bar\"]", AttributeMatchType.EQUALS),
            withExpectedResult("[foo~=\"bar\"]", AttributeMatchType.INCLUDES),
            withExpectedResult("[foo^=\"bar\"]", AttributeMatchType.PREFIXMATCH),
            withExpectedResult("[foo$=\"bar\"]", AttributeMatchType.SUFFIXMATCH),
            withExpectedResult("[foo*=\"bar\"]", AttributeMatchType.SUBSTRINGMATCH),
            withExpectedResult("[foo|=\"en\"]", AttributeMatchType.DASHMATCH),
            withExpectedResult("[foo=bar]", AttributeMatchType.EQUALS),
            withExpectedResult("[foo~=bar]", AttributeMatchType.INCLUDES),
            withExpectedResult("[foo^=bar]", AttributeMatchType.PREFIXMATCH),
            withExpectedResult("[foo$=bar]", AttributeMatchType.SUFFIXMATCH),
            withExpectedResult("[foo*=bar]", AttributeMatchType.SUBSTRINGMATCH),
            withExpectedResult("[foo|=en]", AttributeMatchType.DASHMATCH)
        );

        for (ParseResult<AttributeMatchType> result : results) {
            AttributeSelector selector = expectOnly(result.broadcaster, AttributeSelector.class);
            assertThat(selector.matchType().get())
                .describedAs(result.source.toString())
                .isSameAs(result.expected);
        }
    }

    @Test
    public void noMatchTypeWhenAbsent() {
        GenericParseResult result = parse("[foo]").get(0);
        AttributeSelector selector = expectOnly(result.broadcaster, AttributeSelector.class);
        assertThat(selector.matchType().isPresent()).isFalse();
    }

    @Test
    public void matchesExpectedMatchValue() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("[foo=\"bar\"]", "bar"),
            withExpectedResult("[foo~=\"bar\"]", "bar"),
            withExpectedResult("[foo^=\"bar\"]", "bar"),
            withExpectedResult("[foo$=\"bar\"]", "bar"),
            withExpectedResult("[foo*=\"bar\"]", "bar"),
            withExpectedResult("[foo|=\"en\"]", "en"),
            withExpectedResult("[foo=bar]", "bar"),
            withExpectedResult("[foo~=bar]", "bar"),
            withExpectedResult("[foo^=bar]", "bar"),
            withExpectedResult("[foo$=bar]", "bar"),
            withExpectedResult("[foo*=bar]", "bar"),
            withExpectedResult("[foo|=en]", "en"),
            withExpectedResult("[href=\"http://peri]s[]hab\\\"lepr'ess.com\"]", "http://peri]s[]hab\\\"lepr'ess.com")
        );

        for (ParseResult<String> result : results) {
            AttributeSelector selector = expectOnly(result.broadcaster, AttributeSelector.class);
            assertThat(selector.value().get())
                .describedAs(result.source.toString())
                .isEqualTo(result.expected);
        }
    }

    @Test
    public void noValueWhenAbsent() {
        GenericParseResult result = parse("[foo]").get(0);
        AttributeSelector selector = expectOnly(result.broadcaster, AttributeSelector.class);
        assertThat(selector.value().isPresent()).isFalse();
    }

    @Test
    public void errorsIfNoIdentAfterOpeningBracket() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.EXPECTED_ATTRIBUTE_NAME);
        parse("[");
    }

    @Test
    public void errorsIfInvalidIdentAfterOpeningBracket() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.EXPECTED_ATTRIBUTE_NAME);
        parse("[1");
    }

    @Test
    public void errorsIfMissingClosingBracket() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find");
        parse("[href");
    }

    @Test
    public void errorsIfNoValueAfterMatcher() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.EXPECTED_ATTRIBUTE_MATCH_VALUE);
        parse("[href=");
    }
}
