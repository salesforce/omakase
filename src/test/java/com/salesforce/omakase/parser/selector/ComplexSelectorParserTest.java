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
import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.OrphanedComment;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Combinator;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.PseudoClassSelector;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.ast.selector.TypeSelector;
import com.salesforce.omakase.ast.selector.UniversalSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link ComplexSelectorParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc"})
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
            // ".btn-group > .btn:first-child:not(:last-child):not(.dropdown-toggle)",
            // ".btn-group > .btn-group:not(:first-child):not(:last-child) > .btn",
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
    public boolean allowedToTrimLeadingWhitespace() {
        return true;
    }

    @Test
    @Override
    @SuppressWarnings("unchecked")
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
            withExpectedResult(".class .class /*comment*/", 4),
            withExpectedResult("*::before", 2))
        );

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted)
                .describedAs(result.stream.toString())
                .hasSize(result.expected);
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        GenericParseResult result = Iterables.getOnlyElement(
            parse("*.page .home > .child #id:hover .button .inner + span:before"));

        List<Syntax> broadcasted = result.broadcasted;
        assertThat(broadcasted).hasSize(16);
        assertThat(broadcasted.get(0)).isInstanceOf(UniversalSelector.class);
        assertThat(broadcasted.get(1)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(2)).isInstanceOf(Combinator.class);
        assertThat(broadcasted.get(3)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(4)).isInstanceOf(Combinator.class);
        assertThat(broadcasted.get(5)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(6)).isInstanceOf(Combinator.class);
        assertThat(broadcasted.get(7)).isInstanceOf(IdSelector.class);
        assertThat(broadcasted.get(8)).isInstanceOf(PseudoClassSelector.class);
        assertThat(broadcasted.get(9)).isInstanceOf(Combinator.class);
        assertThat(broadcasted.get(10)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(11)).isInstanceOf(Combinator.class);
        assertThat(broadcasted.get(12)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(13)).isInstanceOf(Combinator.class);
        assertThat(broadcasted.get(14)).isInstanceOf(TypeSelector.class);
        assertThat(broadcasted.get(15)).isInstanceOf(PseudoElementSelector.class);
    }

    @Test
    public void matchesExpectedBroadcastContentWithOrphanedComments() {
        GenericParseResult result = Iterables.getOnlyElement(
            parse(".class/*comment*/ .class /*comment*/\n\n/*comment \n comment */ #id /*comment */"));

        List<Syntax> broadcasted = result.broadcasted;
        assertThat(broadcasted.get(0)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(1)).isInstanceOf(Combinator.class);
        assertThat(broadcasted.get(2)).isInstanceOf(ClassSelector.class);
        assertThat(broadcasted.get(3)).isInstanceOf(Combinator.class);
        assertThat(broadcasted.get(4)).isInstanceOf(IdSelector.class);
        assertThat(broadcasted.get(5)).isInstanceOf(OrphanedComment.class);
    }

    @Test
    public void errorsIfUniversalNotLast() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.NAME_SELECTORS_NOT_ALLOWED.message());
        parse(".class*");
    }

    @Test
    public void errorsIfTrailingCombinator() {
        exception.expect(ParserException.class);
        exception.expectMessage("Trailing combinator");
        parse(".class>").get(0);
    }

    @Test
    public void removesTrailingDescendantCombinatorWithoutError() {
        GenericParseResult result = parse(".class ").get(0);
        assertThat(result.broadcasted).hasSize(1);
        assertThat(result.broadcasted.get(0)).isNotInstanceOf(Combinator.class);
    }

    @Test
    public void orphanedComments() {
        GenericParseResult result = parse(".class /* comment *//* comment */").get(0);
        assertThat(result.broadcasted).hasSize(3);
        assertThat(result.broadcasted.get(1)).isInstanceOf(OrphanedComment.class);
        assertThat(result.broadcasted.get(2)).isInstanceOf(OrphanedComment.class);
        OrphanedComment orphaned = (OrphanedComment)result.broadcasted.get(1);
        assertThat(orphaned.content()).isEqualTo(" comment ");
    }
}
