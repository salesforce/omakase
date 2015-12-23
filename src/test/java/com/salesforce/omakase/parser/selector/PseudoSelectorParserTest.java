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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.PseudoClassSelector;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link PseudoSelectorParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PseudoSelectorParserTest extends AbstractParserTest<PseudoSelectorParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "   ",
            "\n",
            "nth-child",
            " :before",
            " ::after",
            "\n:nth-child",
            "123hover:: :hover"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            ":root",
            ":nth-child(n)",
            ":nth-last-child(n)",
            ":nth-of-type(n)",
            ":nth-last-of-type(n)",
            ":first-child",
            ":last-child",
            ":first-of-type",
            ":last-of-type",
            ":only-child",
            ":only-of-type",
            ":empty",
            ":link",
            ":visited",
            ":hover",
            ":focus",
            ":target",
            ":lang(fr)",
            ":enabled",
            ":disabled",
            ":checked",
            "::first-line",
            "::first-letter",
            "::before",
            "::after",
            ":first-line",
            ":first-letter",
            ":before",
            ":after",
            ":not(s)",
            ":lang(fr-be)",
            ":nth-child(2n+1)",
            ":nth-child(2n+0)",
            ":nth-child(10n+-1)",
            ":nth-child( +3n - 2 )",
            ":not([DISABLED])",
            ":not(.classname)",
            "/*comment*/:not(.classname)"
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult(":root", 5),
            withExpectedResult(":link:after", 5),
            withExpectedResult("::first-line:", 12),
            withExpectedResult(":before :after", 7),
            withExpectedResult(":link.class", 5),
            withExpectedResult(":before/*comment*/", 7),
            withExpectedResult(":enabled>   ", 8),
            withExpectedResult(":nth-child(2n+1)   ", 16),
            withExpectedResult(":nth-child(2n+1)\n", 16),
            withExpectedResult(":nth-child(2n+1)>", 16),
            withExpectedResult(":nth-child(2n+1)1", 16),
            withExpectedResult("::before::selection", 8),
            withExpectedResult(":not([DISABLED]))", 16),
            withExpectedResult("/*comment*/:not([DISABLED]))", 27)
        );
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
        // tests pseudo element
        List<ParseResult<String>> results = parseWithExpected(ImmutableList.of(
            withExpectedResult(":first-line", "first-line"),
            withExpectedResult(":first-letter", "first-letter"),
            withExpectedResult(":before", "before"),
            withExpectedResult(":after", "after"),
            withExpectedResult("::first-line", "first-line"),
            withExpectedResult("::first-letter", "first-letter"),
            withExpectedResult("::before", "before"),
            withExpectedResult("::after", "after")
        ));

        for (ParseResult<String> result : results) {
            PseudoElementSelector selector = result.broadcaster.findOnly(PseudoElementSelector.class).get();
            assertThat(selector.name())
                .describedAs(result.source.toString())
                .isEqualTo(result.expected);
        }
    }

    @Test
    public void matchesExpectedPseudoClassName() {
        List<ParseResult<String>> results = parseWithExpected(ImmutableList.of(
            withExpectedResult(":root", "root"),
            withExpectedResult(":link", "link"),
            withExpectedResult(":nth-child(2n+1)", "nth-child"),
            withExpectedResult(":first-of-type", "first-of-type"),
            withExpectedResult(":nth-child( +3n - 2 )", "nth-child"),
            withExpectedResult(":not([DISABLED])", "not"),
            withExpectedResult(":not(.classname)", "not")
        ));

        for (ParseResult<String> result : results) {
            PseudoClassSelector selector = result.broadcaster.findOnly(PseudoClassSelector.class).get();
            assertThat(selector.name())
                .describedAs(result.source.toString())
                .isEqualTo(result.expected);
        }
    }

    @Test
    public void matchesExpectedPseudoClassArgs() {
        List<ParseResult<String>> results = parseWithExpected(ImmutableList.of(
            withExpectedResult(":nth-child(2n+1)", "2n+1"),
            withExpectedResult(":nth-child( +3n - 2 )", "+3n - 2"),
            withExpectedResult(":not([DISABLED])", "[DISABLED]"),
            withExpectedResult(":not(   .classname123)", ".classname123")
        ));

        for (ParseResult<String> result : results) {
            PseudoClassSelector selector = result.broadcaster.findOnly(PseudoClassSelector.class).get();
            assertThat(selector.args().get())
                .describedAs(result.source.toString())
                .isEqualTo(result.expected);
        }
    }

    @Test
    public void collectsComments() {
        GenericParseResult result = parse("/*comment*/:before").get(0);
        assertThat(Iterables.get(result.broadcastedSyntax, 0).comments()).hasSize(1);
    }

    @Test
    public void errorsIfMissingName() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_PSEUDO_NAME.message());
        parse(":");
    }

    @Test
    public void errorsIfInvalidName() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_PSEUDO_NAME.message());
        parse(":123hover");
    }

    @Test
    public void errorsIfMissingClosingParens() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse(":nth-child(2n+1");
    }
}
