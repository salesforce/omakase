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

package com.salesforce.omakase.parser.raw;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.parser.AbstractParserTest;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;
import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link RawDeclarationSequenceParser}.
 *
 * @author nmcwilliams
 */
public class RawDeclarationSequenceParserTest extends AbstractParserTest<RawDeclarationSequenceParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "   ",
            "\n",
            "{}",
            "---"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "color:red",
            "margin: 1px solid red",
            "color: red; margin: 1px solid",
            "color: red; margin: 1px solid background: red;",
            "color: red; \n margin: 1px solid red;\n",
            "display:none;color:red;    padding: 1px",
            "background: linear-gradient(45deg,rgba(0,0,0,0.24) 0%,rgba(0,0,0,0) 100%); color: blue"
        );
    }

    @Override
    public String validSourceForPositionTesting() {
        return Iterables.get(validSources(), 0);
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return true;
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("color:red;;; color:blue}", 23),
            withExpectedResult("background: linear-gradient(45deg,rgba(0,0,0,0.24) 0%,rgba(0,0,0,0) 100%); color: blue  }", 88));
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<ParseResult<Integer>> results = parseWithExpected(
            withExpectedResult("color:red", 1),
            withExpectedResult("color: red; margin: 1px solid red", 2),
            withExpectedResult("background: linear-gradient(45deg,rgba(0,0,0,0.24) 0%,rgba(0,0,0,0) 100%); color: blue", 2),
            withExpectedResult("display:none;color:red;    padding: 1px", 3));

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(result.expected);
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        GenericParseResult result = parse("color:red; margin:1px").get(0);
        assertThat(result.broadcasted).hasSize(2);
        assertThat(Iterables.get(result.broadcasted, 0)).isInstanceOf(Declaration.class);
        assertThat(Iterables.get(result.broadcasted, 1)).isInstanceOf(Declaration.class);
    }
}
