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

package com.salesforce.omakase.parser.selector;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.AttributeMatchType;
import com.salesforce.omakase.ast.selector.AttributeSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.*;

/**
 * Unit tests for {@link AttributeSelectorParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
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
            AttributeSelector selector = result.broadcaster.findOnly(AttributeSelector.class).get();
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
            AttributeSelector selector = result.broadcaster.findOnly(AttributeSelector.class).get();
            assertThat(selector.matchType().get())
                .describedAs(result.source.toString())
                .isSameAs(result.expected);
        }
    }

    @Test
    public void noMatchTypeWhenAbsent() {
        GenericParseResult result = parse("[foo]").get(0);
        AttributeSelector selector = result.broadcaster.findOnly(AttributeSelector.class).get();
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
            AttributeSelector selector = result.broadcaster.findOnly(AttributeSelector.class).get();
            assertThat(selector.value().get())
                .describedAs(result.source.toString())
                .isEqualTo(result.expected);
        }
    }

    @Test
    public void noValueWhenAbsent() {
        GenericParseResult result = parse("[foo]").get(0);
        AttributeSelector selector = result.broadcaster.findOnly(AttributeSelector.class).get();
        assertThat(selector.value().isPresent()).isFalse();
    }

    @Test
    public void errorsIfNoIdentAfterOpeningBracket() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.EXPECTED_ATTRIBUTE_NAME.message());
        parse("[");
    }

    @Test
    public void errorsIfInvalidIdentAfterOpeningBracket() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.EXPECTED_ATTRIBUTE_NAME.message());
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
        exception.expectMessage(Message.EXPECTED_ATTRIBUTE_MATCH_VALUE.message());
        parse("[href=");
    }
}
