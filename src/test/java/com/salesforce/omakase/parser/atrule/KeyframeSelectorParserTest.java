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

package com.salesforce.omakase.parser.atrule;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.KeyframeSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.*;
import static org.fest.assertions.api.Assertions.assertThat;

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
        for (GenericParseResult result : parse(validSources())) {
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
        exception.expectMessage(Message.MISSING_PERCENTAGE.message());
        parse("0");
    }
}
