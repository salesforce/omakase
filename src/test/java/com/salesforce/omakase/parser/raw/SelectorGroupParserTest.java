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
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link SelectorGroupParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class SelectorGroupParserTest extends AbstractParserTest<SelectorGroupParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "1,2",
            "$class",
            "",
            "\n");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "#abc, #abc",
            ".class, .class",
            "*:hover, ::before",
            "p div, .classname",
            ".-anc",
            "a, :before",
            "/*comment*/ .abc, .abc",
            ".abc,/*comment*/.abc",
            ".abc, .abc /*comment*/");
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult(".class { color : red", 7),
            withExpectedResult(".class, #id { color:red", 12),
            withExpectedResult(".class,#id {color:red", 11),
            withExpectedResult(".class.class.class {\n\n", 19),
            withExpectedResult("*{color: \n red", 1),
            withExpectedResult(".class,\n.class2, \n.class3 {   ", 26));
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<ParseResult<Integer>> results = parseWithExpected(
            withExpectedResult("#abc, #abc", 2),
            withExpectedResult(".class, .class", 2),
            withExpectedResult("*:hover, ::before", 2),
            withExpectedResult(".class.class.class, \n  .class.class.class  ", 2),
            withExpectedResult("p div, .classname", 2),
            withExpectedResult(".class,\n.class2, \n.class3 ", 3));

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(result.expected);
        }
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
        GenericParseResult result = parse("   .class.class.class, \n  .class.class.class  ").get(0);
        assertThat(result.broadcasted).hasSize(2);
        assertThat(Iterables.get(result.broadcasted, 0)).isInstanceOf(Selector.class);
        assertThat(Iterables.get(result.broadcasted, 1)).isInstanceOf(Selector.class);
    }

    @Test
    public void errorsOnTrailingComma() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find a selector");
        parse("#abc,#abc, ");
    }
}
