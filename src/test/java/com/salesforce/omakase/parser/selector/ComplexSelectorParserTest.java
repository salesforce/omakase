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
import com.google.common.collect.Lists;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Combinator;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.PseudoClassSelector;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.ast.selector.TypeSelector;
import com.salesforce.omakase.ast.selector.UniversalSelector;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;

/**
 * Unit tests for {@link ComplexSelectorParser}.
 *
 * @author nmcwilliams
 */
public class ComplexSelectorParserTest extends AbstractParserTest<ComplexSelectorParser> {

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of("$anc", "    ", "\n\n\n", "");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "#id",
            ".class",
            "p",
            "a:link",
            "a:hover",
            "div:hover",
            "::selection",
            ":hover",
            "*",
            "*#id",
            "*.class",
            "*:hover",
            "*:before",
            "*::before",
            ".class1.class2.class3.class4",
            "#id#id2#id3",
            "p[foo]",
            "a[foo=\"bar\"]",
            "a[foo=\"bar\"][bar=foo]",
            "a[foo~=\"bar\"]",
            "a[foo^=\"bar\"]",
            "a[foo$=\"bar\"][foo$=\"bar\"][foo$=\"bar\"][foo$=\"bar\"]",
            "a[foo*=\"bar\"]",
            "a[foo|=\"en\"]>a[foo|=\"en\"] a[foo|=\"en\"]",
            "input[type=\"search\"]::-webkit-search-cancel-button",
            ":root",
            " #id.class:first-child",
            ".page .home > .child #id:hover .button .inner + span",
            ".page .home > .child #id:hover .button .inner + span",
            "      ul.gallery li:last-child",
            ".panel-primary > .panel-heading",
            ".ABCCLASS P",
            "  .col-lg-push-0",
            ".table caption + thead tr:first-child th",
            ".table > thead > tr > td.active",
            ".table-hover > tbody > tr > td.warning:hover",
            "  .table-responsive  >  .table-bordered > thead > tr > th:last-child",
            "fieldset[disabled] input[type=\"checkbox\"]",
            "fieldset[disabled] .btn-info:focus",
            "button:not([DISABLED])",
            "button:not(.test)",
            ".btn-group > .btn:first-child:not(:last-child):not(.dropdown-toggle)",
            ".btn-group > .btn-group:not(:first-child):not(:last-child) > .btn",
            "tr:nth-child(2n+1)",
            "tr:nth-child(2n+0) > tr:nth-child(2n+0)",
            "tr:nth-child(even)",
            "p:nth-child(4n+4)",
            "*:nth-child(4n+4)",
            "tr:nth-last-child(-n+2)",
            "img:nth-of-type(2n+1)",
            "body > h2:nth-of-type(n+2):nth-last-of-type(n+2)",
            "body > h2:not(:first-of-type):not(:last-of-type)",
            "td:last-of-type",
            "dl :nth-child(2)",
            "div p:empty",
            ".input-group-btn:first-child > .dropdown-toggle",
            ".class /*comment*/ .class",
            ".class/*comment*/ .class",
            ".class /*comment*/.class",
            ".class/*comment*/.class",
            ".class\n/*comment*/\n.class",
            ".class.class/*comment*/",
            ".class.class /*comment*/",
            ".class.class\n /*comment*/"
        );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult(".class .class2", 14),
            withExpectedResult(".class.class2", 13),
            withExpectedResult(".class-abc-abc", 14),
            withExpectedResult(".claz#id", 8),
            withExpectedResult(".claz/*comment*/#id", 19),
            withExpectedResult(".claz#id/*comment*/", 19),
            withExpectedResult(".claz#id/*comment*//*comment*/", 30)
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

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<ParseResult<Integer>> results = parseWithExpected(ImmutableList.of(
            withExpectedResult("#id", 1),
            withExpectedResult(".class1.class2.class3.class4", 4),
            withExpectedResult("#id.class:first-child", 3),
            withExpectedResult(".page .home > .child #id:hover .button .inner + span", 14),
            withExpectedResult(".table-hover  >  tbody > tr > td.warning:hover", 9),
            withExpectedResult(".ABCCLASS P", 3),
            withExpectedResult("div:hover", 2),
            withExpectedResult(".input-group-btn:first-child > .dropdown-toggle", 4),
            withExpectedResult("      ul.gallery li:last-child", 5),
            withExpectedResult(".class /*comment*/ .class", 3),
            withExpectedResult(".class/*comment*/.class", 2),
            withExpectedResult(".class\n/*comment*/\n.class", 3),
            withExpectedResult(".class /*comment*//*comment*/ .class", 3),
            withExpectedResult(".class .class /*comment*/", 3),
            withExpectedResult("*::before", 2))
        );

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted)
                .describedAs(result.source.toString())
                .hasSize(result.expected);
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        GenericParseResult result = Iterables.getOnlyElement(
            parse("*.page .home > .child #id:hover .button .inner + span:before"));

        Iterable<Broadcastable> broadcasted = result.broadcasted;
        assertThat(broadcasted).hasSize(16);
        assertThat(Iterables.get(result.broadcasted, 0)).isInstanceOf(UniversalSelector.class);
        assertThat(Iterables.get(result.broadcasted, 1)).isInstanceOf(ClassSelector.class);
        assertThat(Iterables.get(result.broadcasted, 2)).isInstanceOf(Combinator.class);
        assertThat(Iterables.get(result.broadcasted, 3)).isInstanceOf(ClassSelector.class);
        assertThat(Iterables.get(result.broadcasted, 4)).isInstanceOf(Combinator.class);
        assertThat(Iterables.get(result.broadcasted, 5)).isInstanceOf(ClassSelector.class);
        assertThat(Iterables.get(result.broadcasted, 6)).isInstanceOf(Combinator.class);
        assertThat(Iterables.get(result.broadcasted, 7)).isInstanceOf(IdSelector.class);
        assertThat(Iterables.get(result.broadcasted, 8)).isInstanceOf(PseudoClassSelector.class);
        assertThat(Iterables.get(result.broadcasted, 9)).isInstanceOf(Combinator.class);
        assertThat(Iterables.get(result.broadcasted, 10)).isInstanceOf(ClassSelector.class);
        assertThat(Iterables.get(result.broadcasted, 11)).isInstanceOf(Combinator.class);
        assertThat(Iterables.get(result.broadcasted, 12)).isInstanceOf(ClassSelector.class);
        assertThat(Iterables.get(result.broadcasted, 13)).isInstanceOf(Combinator.class);
        assertThat(Iterables.get(result.broadcasted, 14)).isInstanceOf(TypeSelector.class);
        assertThat(Iterables.get(result.broadcasted, 15)).isInstanceOf(PseudoElementSelector.class);
    }

    @Test
    public void matchesExpectedBroadcastContentWithOrphanedComments() {
        GenericParseResult result = Iterables.getOnlyElement(
            parse(".class/*comment*/ .class /*comment*/\n\n/*comment \n comment */ #id /*comment */"));

        List<Broadcastable> broadcasted = Lists.newArrayList(result.broadcasted);
        assertThat(broadcasted.get(0)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(1)).isInstanceOf(Combinator.class);
        assertThat(broadcasted.get(2)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(3)).isInstanceOf(Combinator.class);
        assertThat(broadcasted.get(4)).isInstanceOf(IdSelector.class);
    }

    @Test
    public void errorsIfUniversalNotLast() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.NAME_SELECTORS_NOT_ALLOWED);
        parse(".class*");
    }

    @Test
    public void errorsIfTrailingCombinator() {
        exception.expect(ParserException.class);
        exception.expectMessage("Trailing combinator");
        parse(".class>").get(0);
    }

    @Test
    public void errorsIfTrailingCombinator2() {
        exception.expect(ParserException.class);
        exception.expectMessage("Trailing combinator");
        parse(".page .home > .child #id:hover .button .inner + span>").get(0);
    }
    @Test
    public void removesTrailingDescendantCombinatorWithoutError() {
        GenericParseResult result = parse(".class ").get(0);
        assertThat(result.broadcasted).hasSize(1);
        assertThat(Iterables.get(result.broadcasted, 0)).isNotInstanceOf(Combinator.class);
    }

    @Test
    public void removesTrailingDescendantCombinatorWithoutError2() {
        GenericParseResult result = parse(".page .home > .child #id:hover .button .inner + span.class ").get(0);
        assertThat(result.broadcasted).hasSize(15);
        assertThat(Iterables.get(result.broadcasted, 0)).isNotInstanceOf(Combinator.class);
    }
}
