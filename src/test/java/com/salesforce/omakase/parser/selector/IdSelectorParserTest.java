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
