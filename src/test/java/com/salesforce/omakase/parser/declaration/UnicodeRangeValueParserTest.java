/*
 * Copyright (C) 2014 salesforce.com, inc.
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
import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.UnicodeRangeValue;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.*;
import static org.fest.assertions.api.Assertions.*;

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
            UnicodeRangeValue range = result.broadcaster.findOnly(UnicodeRangeValue.class).get();
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
        exception.expectMessage(Message.UNICODE_LONG.message());
        parse("u+fffffff");
    }

    @Test
    public void errorsIfTooManyWildcards() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.UNICODE_LONG.message());
        parse("u+???????");
    }

    @Test
    public void errorsIfHexAfterWildcard() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.HEX_AFTER_WILDCARD.message());
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
        exception.expectMessage(Message.UNICODE_LONG.message());
        parse("u+ff0-7777777");
    }

    @Test
    public void errorsOnWildcardInFirstRange() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.WILDCARD_NOT_ALLOWED.message());
        parse("u+ff0?-f00");
    }

    @Test
    public void errorsOnWildcardInEndRange() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.WILDCARD_NOT_ALLOWED.message());
        parse("u+ff0-f0?0");
    }
}
