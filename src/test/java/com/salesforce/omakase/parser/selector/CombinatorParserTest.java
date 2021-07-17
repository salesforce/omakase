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

package com.salesforce.omakase.parser.selector;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.Combinator;
import com.salesforce.omakase.ast.selector.SelectorPartType;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;

/**
 * Unit tests for {@link CombinatorParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class CombinatorParserTest extends AbstractParserTest<CombinatorParser> {

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            ".class",
            "#id",
            "p div"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            " ",
            "   ",
            ">",
            " >",
            "+",
            " +",
            "~",
            " ~",
            "\n",
            "\n  ",
            "  \n  ",
            "\t",
            "\t  "
        );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult(" .class", 1),
            withExpectedResult("  .class", 2),
            withExpectedResult("> .class", 2),
            withExpectedResult(" > .class", 3),
            withExpectedResult(">.class", 1),
            withExpectedResult("   +   .class", 7),
            withExpectedResult("   +.class", 4),
            withExpectedResult("~.class", 1));
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
        List<ParseResult<SelectorPartType>> results = parseWithExpected(
            withExpectedResult(" .class", SelectorPartType.DESCENDANT_COMBINATOR),
            withExpectedResult("   .class", SelectorPartType.DESCENDANT_COMBINATOR),
            withExpectedResult("> .class", SelectorPartType.CHILD_COMBINATOR),
            withExpectedResult(" > .class", SelectorPartType.CHILD_COMBINATOR),
            withExpectedResult(">.class", SelectorPartType.CHILD_COMBINATOR),
            withExpectedResult("+ div", SelectorPartType.ADJACENT_SIBLING_COMBINATOR),
            withExpectedResult(" + .class", SelectorPartType.ADJACENT_SIBLING_COMBINATOR),
            withExpectedResult("    +    .class", SelectorPartType.ADJACENT_SIBLING_COMBINATOR),
            withExpectedResult("+.class", SelectorPartType.ADJACENT_SIBLING_COMBINATOR),
            withExpectedResult("~ .class", SelectorPartType.GENERAL_SIBLING_COMBINATOR),
            withExpectedResult(" ~ #id", SelectorPartType.GENERAL_SIBLING_COMBINATOR),
            withExpectedResult("~.class", SelectorPartType.GENERAL_SIBLING_COMBINATOR),
            withExpectedResult("\n.class", SelectorPartType.DESCENDANT_COMBINATOR),
            withExpectedResult("\t.class", SelectorPartType.DESCENDANT_COMBINATOR));

        for (ParseResult<SelectorPartType> result : results) {
            Combinator combinator = expectOnly(result.broadcaster, Combinator.class);
            assertThat(combinator.type()).describedAs(result.source.toString()).isEqualTo(result.expected);
        }
    }

    @Test
    /* overridden because whitespace can be a descendant combinator */
    public void correctLineAndColumnNumber() {
        for (GenericParseResult result : parse(validSources(), false)) {
            Syntax syntax = Iterables.get(result.broadcastedSyntax, 0);
            assertThat(syntax.line())
                .describedAs(result.source.toString())
                .isEqualTo(1);
            assertThat(syntax.column())
                .describedAs(result.source.toString())
                .isEqualTo(1);
        }
        
        for (GenericParseResult result : parse(validSources(), true)) {
            Syntax syntax = Iterables.get(result.broadcastedSyntax, 0);
            assertThat(syntax.line())
                .describedAs(result.source.toString())
                .isEqualTo(1);
            assertThat(syntax.column())
                .describedAs(result.source.toString())
                .isEqualTo(1);
        }
    }
}
