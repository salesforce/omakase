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
import com.salesforce.omakase.ast.selector.TypeSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.test.util.Templates.SourceWithExpectedResult;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link TypeSelectorParser}.
 *
 * @author nmcwilliams
 */
public class TypeSelectorParserTest extends AbstractParserTest<TypeSelectorParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "#id",
            ".class",
            ".p",
            ".class div",
            " div",
            "*");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "p",
            "div",
            "a");
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("p div", 1),
            withExpectedResult("p#div", 1),
            withExpectedResult("div.class", 3),
            withExpectedResult("div div div", 3),
            withExpectedResult("a:link", 1));
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return false;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("p", "p"),
            withExpectedResult("P", "p"),
            withExpectedResult("div", "div"),
            withExpectedResult("DIV", "div"),
            withExpectedResult("A", "a"),
            withExpectedResult("somethingnew", "somethingnew"));

        for (ParseResult<String> result : results) {
            TypeSelector selector = result.broadcaster.findOnly(TypeSelector.class).get();
            assertThat(selector.name())
                .describedAs(result.stream.toString())
                .isEqualTo(result.expected);
        }
    }
}
