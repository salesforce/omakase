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

package com.salesforce.omakase.parser.declaration;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.OrphanedComment;
import com.salesforce.omakase.ast.declaration.value.FunctionValue;
import com.salesforce.omakase.ast.declaration.value.KeywordValue;
import com.salesforce.omakase.ast.declaration.value.NumericalValue;
import com.salesforce.omakase.ast.declaration.value.TermList;
import com.salesforce.omakase.ast.declaration.value.TermListMember;
import com.salesforce.omakase.ast.declaration.value.TermOperator;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link TermListParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc"})
public class TermListParserTest extends AbstractParserTest<TermListParser> {

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of("$$$", "    ", "\n\n\n", "");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList
            .of(
                "0",
                "0 0",
                "0, 0",
                "1px",
                " 1px 1px",
                "1px / 1px",
                "1px, 33px, 40px, 50px",
                "1em, 3pt #abcabc red red",
                "url(afafafa) left 60% no-repeat",
                "Times, \"Times New Roman\", sans-serif",
                "top",
                "none",
                "top right bottom left",
                "1 3 3px 2 / 1 2 3px 1em",
                " 50%",
                "8 auto",
                "1px\n1px",
                "1px\t1px",
                "  #ffcc11",
                "1px solid red",
                "rgb(0, 255, 255)",
                "rgba(0, 255, 255, 1%)",
                "0 1px 3px rgba(0, 0, 0, 0.7),0 1px 0 rgba(0, 0, 0, 0.3)",
                "-webkit-gradient(linear, 0% 0%, 0% 100%, from(#F8F8F9), to(#DDDFE1))",
                "#757D8A -webkit-gradient(linear, 0% 0%, 0% 100%, from(#7F8792), to(#535B68))",
                "rotateX(80deg) rotateY(0deg) rotateZ(0deg)",
                "shadow infinite 7s ease",
                "linear-gradient(45deg,rgba(0,0,0,0.24) 0%,rgba(0,0,0,0) 100%)",
                "-1px 1px 0 #222",
                "\" (\" attr(href) \")\"",
                "'\\2014 \\00A0'",
                "66.66666666666666%",
                "linear-gradient(45deg, rgba(255, 255, 255, 0.15) 25%, transparent 25%, transparent 50%, rgba(255, 255, 255, " +
                    "0.15) 50%, rgba(255, 255, 255, 0.15) 75%, transparent 75%, transparent)",
                "63px 63px 63px 63px / 108px 108px 72px 72px",
                "0 0 0 1em red,\n     0 1em 0 1em red,\n     -2.5em 1.5em 0 .5em red,\n     2.5em 1.5em 0 .5em red," +
                    "\n     -3em -3em 0 0 red\n",
                "1px /*x*/ 1px 1px 1px",
                "1px  1px 1px  /*x*/1px",
                "1px /*x*/ 1px 1px 1px/*x*/",
                "/*x*/1px /*x*/ 1px 1px 1px",
                "linear-gradient(45deg,/*x*/rgba(0,0,0,0.24) 0%,/*)*/rgba(0,0,0,0) 100%)"
            );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("0", 1),
            withExpectedResult("#ffcc11 ", 8),
            withExpectedResult("1px 1px $ 1px", 8),
            withExpectedResult("rotateX(80deg) rotateY(0deg) rotateZ(0deg)", 42),
            withExpectedResult("-1px 1px 0 #222", 15));
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return true;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<ParseResult<Integer>> results = parseWithExpected(ImmutableList.of(
            withExpectedResult("0", 1),
            withExpectedResult("#ffcc11 ", 1),
            withExpectedResult("1px\n1px", 2),
            withExpectedResult("1px\t1px", 2),
            withExpectedResult("rotateX(80deg) rotateY(0deg) rotateZ(0deg)", 3),
            withExpectedResult("-1px 1px 0 #222", 4),
            withExpectedResult("0 1px 3px rgba(0, 0, 0, 0.7),0 1px 0 rgba(0, 0, 0, 0.3)", 8),
            withExpectedResult("-1px 1px 0 #222", 4)));

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted)
                .describedAs(result.stream.toString())
                .hasSize(result.expected + 1); // +1 to account for the term list broadcast
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        GenericParseResult result = Iterables
            .getOnlyElement(parse("0 1px\n3px /1em   rgba(0, 0, 0, 0.7),0 1px , 0 rgba(0, 0, 0, 0.3) "));

        TermList tl = result.broadcaster.find(TermList.class).get();
        List<TermListMember> members = tl.members();

        // the last space should NOT count as an operator. also, multiple spaces should not count as multiple operators
        assertThat(members).hasSize(17);

        assertThat(members.get(0)).isInstanceOf(NumericalValue.class);
        assertThat(members.get(1)).isInstanceOf(TermOperator.class);
        assertThat(members.get(2)).isInstanceOf(NumericalValue.class);
        assertThat(members.get(3)).isInstanceOf(TermOperator.class);
        assertThat(members.get(4)).isInstanceOf(NumericalValue.class);
        assertThat(members.get(5)).isInstanceOf(TermOperator.class);
        assertThat(members.get(6)).isInstanceOf(NumericalValue.class);
        assertThat(members.get(7)).isInstanceOf(TermOperator.class);
        assertThat(members.get(8)).isInstanceOf(FunctionValue.class);
        assertThat(members.get(9)).isInstanceOf(TermOperator.class);
        assertThat(members.get(10)).isInstanceOf(NumericalValue.class);
        assertThat(members.get(11)).isInstanceOf(TermOperator.class);
        assertThat(members.get(12)).isInstanceOf(NumericalValue.class);
        assertThat(members.get(13)).isInstanceOf(TermOperator.class);
        assertThat(members.get(14)).isInstanceOf(NumericalValue.class);
        assertThat(members.get(15)).isInstanceOf(TermOperator.class);
        assertThat(members.get(16)).isInstanceOf(FunctionValue.class);
    }

    @Test
    public void matchesExpectedBroadcastContentWithOrphanedComments() {
        GenericParseResult result = Iterables.getOnlyElement(parse("/*x*/ 1px /*x*/ solid red /*x*/\n/*x*/"));

        List<Broadcastable> broadcasted = result.broadcasted;

        assertThat(broadcasted).hasSize(6);
        assertThat(broadcasted.get(0)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(1)).isInstanceOf(KeywordValue.class);
        assertThat(broadcasted.get(2)).isInstanceOf(KeywordValue.class);
        assertThat(broadcasted.get(3)).isInstanceOf(OrphanedComment.class);
        assertThat(broadcasted.get(4)).isInstanceOf(OrphanedComment.class);
        assertThat(broadcasted.get(5)).isInstanceOf(TermList.class);
    }

    @Test
    public void mustParseFullStream() {
        List<GenericParseResult> parse = parse(ImmutableList.of(
            "1px 1px",
            "\"Arial\"",
            "  top right"
        ));

        for (GenericParseResult result : parse) {
            assertThat(result.stream.eof()).isTrue();
        }
    }

    @Test
    public void errorsIfTrailingComma() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find another term");
        parse("1px, ");
    }

    @Test
    public void errorsIfTrailingSlash() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find another term");
        parse("1px 1px 1px 1px / ");
    }

    @Test
    public void errorsIfNestedCommentWithoutOperator() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_OPERATOR_NEAR_COMMENT.message());
        parse("1px /*x*/ 1px 1px/*x*/1px");
    }

    @Test
    public void parsesImportant() {
        List<String> sourcesWithSpace = new ArrayList<>();
        List<String> sourcesWithoutSpace = new ArrayList<>();

        for (String source : validSources()) {
            sourcesWithSpace.add(source + " !important");
            sourcesWithoutSpace.add(source + "!IMPORTANT");
        }

        for (GenericParseResult result : parse(sourcesWithSpace)) {
            assertThat(result.stream.eof()).describedAs(result.stream.toString()).isTrue();
            assertThat(result.success).describedAs(result.stream.toString()).isTrue();
        }

        for (GenericParseResult result : parse(sourcesWithoutSpace)) {
            assertThat(result.stream.eof()).describedAs(result.stream.toString()).isTrue();
            assertThat(result.success).describedAs(result.stream.toString()).isTrue();
        }
    }
}
