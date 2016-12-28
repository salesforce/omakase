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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.UnicodeRangeValue;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link UnicodeRangeValueParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "SpellCheckingInspection"})
public class UnicodeRangeValueParserTest extends AbstractParserTest<UnicodeRangeValueParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "uu+ff0",
            "Uu+ff0",
            "u-ff0",
            "u ff0",
            "uff0",
            "u\\ff0",
            "\"u+ff0\"",
            "u +ff0",
            "u\n+ff0",
            "ff0",
            "u",
            "U"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "U+26",
            "u+0025-00FF",
            "U+4??",
            "U+416",
            "u+400-4ff",
            "U+4??",
            "U+2000-27FF",
            "U+1D400-1D7FF",
            "U+ff??",
            "u+1e00-1fff",
            "U+???",
            "U+0???",
            "U+0000-0FFF",
            "U+?",
            "U+??",
            "U+???",
            "U+????",
            "U+?????",
            "U+??????",
            "u+00000?",
            "u+0fa???"
        );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("U+26;", 4),
            withExpectedResult("U+0025-00FF    ", 11),
            withExpectedResult("U+1D400-1D7FF\n", 13),
            withExpectedResult("U+0???}", 6),
            withExpectedResult("U+???", 5),
            withExpectedResult("U+??????", 8),
            withExpectedResult("u+0fa???,U+000-5FF, U+1e00-1fff, U+2000-2300", 8),
            withExpectedResult("U+A5, U+4E00-9FFF, U+30??, U+FF00-FF9F", 4),
            withExpectedResult("U+00-FF   , U+980-9FF", 7));
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
            withExpectedResult("U+26", "u+26"),
            withExpectedResult("U+0025-00FF", "u+0025-00ff"),
            withExpectedResult("u+1D400-1D7FF", "u+1d400-1d7ff"),
            withExpectedResult("U+???", "u+???"),
            withExpectedResult("u+0fa???,U+000-5FF", "u+0fa???"),
            withExpectedResult("u+0fa???", "u+0fa???"));

        for (ParseResult<String> result : results) {
            UnicodeRangeValue range = expectOnly(result.broadcaster, UnicodeRangeValue.class);
            assertThat(range.value()).isEqualTo(result.expected);
        }
    }

    @Test
    public void errorsIfMissingHexAfterUPlus() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find hexidecimal");
        parse("u+");
    }

    @Test
    public void errorsIfSpaceAfterUPlus() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find hexidecimal");
        parse("u+ 11");
    }

    @Test
    public void errorsOnLongLength() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.UNICODE_LONG);
        parse("u+fffffff");
    }

    @Test
    public void errorsIfTooManyWildcards() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.UNICODE_LONG);
        parse("u+???????");
    }

    @Test
    public void errorsIfHexAfterWildcard() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.HEX_AFTER_WILDCARD);
        parse("u+??ff");
    }

    @Test
    public void errorsIfMissingSecondPartOfRange() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find hexidecimal");
        parse("u+ff-");
    }

    @Test
    public void errorsOnLongLengthInEndRange() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.UNICODE_LONG);
        parse("u+ff0-7777777");
    }

    @Test
    public void errorsOnWildcardInFirstRange() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.WILDCARD_NOT_ALLOWED);
        parse("u+ff0?-f00");
    }

    @Test
    public void errorsOnWildcardInEndRange() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.WILDCARD_NOT_ALLOWED);
        parse("u+ff0-f0?0");
    }
}
