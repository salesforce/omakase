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
import com.salesforce.omakase.ast.selector.KeyframeSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.*;
import static org.fest.assertions.api.Assertions.*;

/**
 * TODO description
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
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
        assertThat(Iterables.get(result.broadcasted, 0)).isInstanceOf(Selector.class);
        assertThat(Iterables.get(result.broadcasted, 1)).isInstanceOf(KeyframeSelector.class);
        assertThat(Iterables.get(result.broadcasted, 2)).isInstanceOf(Selector.class);
        assertThat(Iterables.get(result.broadcasted, 3)).isInstanceOf(KeyframeSelector.class);
        assertThat(Iterables.get(result.broadcasted, 4)).isInstanceOf(Selector.class);
        assertThat(Iterables.get(result.broadcasted, 5)).isInstanceOf(KeyframeSelector.class);
        assertThat(Iterables.get(result.broadcasted, 6)).isInstanceOf(Selector.class);
        assertThat(Iterables.get(result.broadcasted, 7)).isInstanceOf(KeyframeSelector.class);
    }

    @Test
    public void errorsOnTrailingComma() {
        exception.expect(ParserException.class);
        exception.expectMessage("Unexpected trailing");
        parse("50%, 60%, , 70% ");
    }
}
