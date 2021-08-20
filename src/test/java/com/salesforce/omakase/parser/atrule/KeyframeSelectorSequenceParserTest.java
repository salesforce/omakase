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

package com.salesforce.omakase.parser.atrule;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.selector.KeyframeSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;

/**
 * Unit tests for {@link KeyframeSelectorSequenceParserTest}.
 *
 * @author nmcwilliams
 */
public class KeyframeSelectorSequenceParserTest extends AbstractParserTest<KeyframeSelectorSequenceParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "  ",
            "\n",
            "$",
            "blah",
            "#id#id",
            "%50"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "50%, 50%",
            "from",
            "to",
            "50%",
            "100%,20%",
            "100%,    20%",
            "20%    ,    50%",
            "20%    ,50%",
            "10%,20%, 30%",
            "/*comment*/50%",
            "50%/*comment*/"
        );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("50%, 50%{", 8),
            withExpectedResult("from {", 5),
            withExpectedResult("100% {", 5),
            withExpectedResult("100%%", 4));
    }

    @Override
    public String validSourceForPositionTesting() {
        return Iterables.get(validSources(), 0);
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return true;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<ParseResult<Integer>> results = parseWithExpected(
            withExpectedResult("50%, 50%", 4),
            withExpectedResult("from", 2),
            withExpectedResult("10%,20%, 30%", 6),
            withExpectedResult("to", 2));

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(result.expected);
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        GenericParseResult result = parse("   from, 20%, 30%, \n70%").get(0);
        assertThat(result.broadcasted).hasSize(8);
        assertThat(Iterables.get(result.broadcasted, 0)).isInstanceOf(KeyframeSelector.class);
        assertThat(Iterables.get(result.broadcasted, 1)).isInstanceOf(Selector.class);
        assertThat(Iterables.get(result.broadcasted, 2)).isInstanceOf(KeyframeSelector.class);
        assertThat(Iterables.get(result.broadcasted, 3)).isInstanceOf(Selector.class);
        assertThat(Iterables.get(result.broadcasted, 4)).isInstanceOf(KeyframeSelector.class);
        assertThat(Iterables.get(result.broadcasted, 5)).isInstanceOf(Selector.class);
        assertThat(Iterables.get(result.broadcasted, 6)).isInstanceOf(KeyframeSelector.class);
        assertThat(Iterables.get(result.broadcasted, 7)).isInstanceOf(Selector.class);
    }

    @Test
    public void errorsOnTrailingComma() {
        exception.expect(ParserException.class);
        exception.expectMessage("Unexpected trailing");
        parse("50%, 60%, , 70% ");
    }
}
