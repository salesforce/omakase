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
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link IdSelectorParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class IdSelectorParserTest extends AbstractParserTest<IdSelectorParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            ".class",
            "p div",
            "a:link",
            "._class",
            ".class #id",
            "p#id",
            " #id");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "#id",
            "#ID",
            "#_id",
            "#_1",
            "#_1id",
            "#id123",
            "#-name",
            "#-NAMEname1_aAz234ABCdefafklsjfseufhuise____hfie");
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("#id .class", 3),
            withExpectedResult("#ID #id", 3),
            withExpectedResult("#_id>p>div#id", 4),
            withExpectedResult("#_1  ", 3),
            withExpectedResult("#_1id", 5),
            withExpectedResult("#id123", 6),
            withExpectedResult("#-name~a", 6),
            withExpectedResult("#-NAMEname1_aAz234ABCdefafklsjfseuf+.huise____hfie", 35));
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
            withExpectedResult("#id .class", "id"),
            withExpectedResult("#ID #id", "ID"),
            withExpectedResult("#_id>p>div#id", "_id"),
            withExpectedResult("#_1  ", "_1"),
            withExpectedResult("#_1id", "_1id"),
            withExpectedResult("#id123", "id123"),
            withExpectedResult("#-name~a", "-name"),
            withExpectedResult("#-NAMEname1_aAz234ABCdefafklsjfseuf+.huise____hfie", "-NAMEname1_aAz234ABCdefafklsjfseuf"));

        for (ParseResult<String> result : results) {
            IdSelector selector = result.broadcaster.findOnly(IdSelector.class).get();
            assertThat(selector.name())
                .describedAs(result.source.toString())
                .isEqualTo(result.expected);
        }
    }

    @Test
    public void errorsIfDoubleHash() {
        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid id name");
        parse("##id");
    }

    @Test
    public void errorsIfDoubleDash() {
        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid id name");
        parse("#--abc");
    }

    @Test
    public void errorsIfDashNumber() {
        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid id name");
        parse("#-1abc");
    }

    @Test
    public void errorsIfDashDot() {
        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid id name");
        parse("#.class");
    }

    @Test
    public void errorsIfSpace() {
        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid id name");
        parse("# abc");
    }
}
