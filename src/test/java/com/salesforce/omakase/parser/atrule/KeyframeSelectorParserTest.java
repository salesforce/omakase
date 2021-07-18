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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.KeyframeSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;

/**
 * Unit tests for {@link KeyframeSelectorParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class KeyframeSelectorParserTest extends AbstractParserTest<KeyframeSelectorParser> {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "  ",
            "\n",
            "blah",
            "fro m",
            "div",
            "#id",
            "    \n ^"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "from",
            "to",
            "0%",
            "50%",
            "100%",
            "  50%",
            "  from",
            "\n to",
            "/*comment*/from"
        );
    }

    @Override
    public void matchesExpectedBroadcastCount() {
        for (GenericParseResult result : parse(validSources(), false)) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(2);
        }
        
        for (GenericParseResult result : parse(validSources(), true)) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(2);
        }
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("from  ", 4),
            withExpectedResult("to to", 2),
            withExpectedResult("50%,50%", 3),
            withExpectedResult("100%", 4));
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
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("from", "from"),
            withExpectedResult("to", "to"),
            withExpectedResult("50%", "50%"),
            withExpectedResult("50%, 50%", "50%"),
            withExpectedResult("50%50%", "50%"),
            withExpectedResult("/**test*/100%", "100%"),
            withExpectedResult("10.5%", "10.5%")
        );

        for (ParseResult<String> result : results) {
            KeyframeSelector kf = result.broadcaster.find(KeyframeSelector.class).get();
            assertThat(kf.keyframe()).isEqualTo(result.expected);
        }
    }

    @Test
    public void errorsIfMissingPercentage() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_PERCENTAGE);
        parse("0");
    }
}
