/*
 * Copyright (c) 2017, salesforce.com, inc.
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
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.selector.SelectorSequenceParser;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link SelectorSequenceParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class SelectorSequenceParserTest extends AbstractParserTest<SelectorSequenceParser> {
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
            "[hidden]",
            "[href^=\"#\"]",
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
