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
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.declaration.GenericFunctionValue;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.Operator;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link TermSequenceParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class TermSequenceParserTest extends AbstractParserTest<TermSequenceParser> {
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
                "linear-gradient(45deg,/*x*/rgba(0,0,0,0.24) 0%,/*)*/rgba(0,0,0,0) 100%)",
                "U+000-49F",
                "U+000-49F, U+2000-27FF,\nU+2900-2BFF, U+1D400-1D7FF",
                "U+000-49F, U+27FF ,  U+29??, U+1D400-1D7FF"
            );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("0", 1),
            withExpectedResult("#ffcc11 ", 8),
            withExpectedResult("1px 1px $ 1px", 8),
            withExpectedResult("rotateX(80deg) rotateY(0deg) rotateZ(0deg)", 42),
            withExpectedResult("-1px 1px 0 #222!important", 15)); // doesn't parse !important
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
        GenericParseResult result = Iterables
            .getOnlyElement(parse("0 1px\n3px /1em   rgba(0, 0, 0, 0.7),0 1px , 0 rgba(0, 0, 0, 0.3) "));

        List<Broadcastable> broadcasted = Lists.newArrayList(result.broadcasted);

        // the last space should NOT count as an operator. also, multiple spaces should not count as multiple operators
        assertThat(broadcasted).hasSize(19);

        assertThat(broadcasted.get(0)).isInstanceOf(RawFunction.class);
        assertThat(broadcasted.get(1)).isInstanceOf(RawFunction.class);
        assertThat(broadcasted.get(2)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(3)).isInstanceOf(Operator.class);
        assertThat(broadcasted.get(4)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(5)).isInstanceOf(Operator.class);
        assertThat(broadcasted.get(6)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(7)).isInstanceOf(Operator.class);
        assertThat(broadcasted.get(8)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(9)).isInstanceOf(Operator.class);
        assertThat(broadcasted.get(10)).isInstanceOf(GenericFunctionValue.class);
        assertThat(broadcasted.get(11)).isInstanceOf(Operator.class);
        assertThat(broadcasted.get(12)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(13)).isInstanceOf(Operator.class);
        assertThat(broadcasted.get(14)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(15)).isInstanceOf(Operator.class);
        assertThat(broadcasted.get(16)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(17)).isInstanceOf(Operator.class);
        assertThat(broadcasted.get(18)).isInstanceOf(GenericFunctionValue.class);
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<ParseResult<Integer>> results = parseWithExpected(ImmutableList.of(
            withExpectedResult("0", 1),
            withExpectedResult("#ffcc11 ", 1),
            withExpectedResult("1px\n1px !important", 3),
            withExpectedResult("1px\t1px", 3),
            withExpectedResult("rotateX(80deg) rotateY(0deg) rotateZ(0deg)", 8), // RawFunction adds each
            withExpectedResult("-1px 1px 0 #222", 7),
            withExpectedResult("0 1px 3px rgba(0, 0, 0, 0.7),0 1px 0 rgba(0, 0, 0, 0.3)", 17), // RawFunction adds 1 each
            withExpectedResult("U+000-49F, U+27FF ,  U+29??,   U+1D400-1D7FF", 7),
            withExpectedResult("-1px 1px 0 #222", 7)));

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted)
                .describedAs(result.source.toString())
                .hasSize(result.expected);
        }
    }

    @Test
    public void handlesComments() {
        GenericParseResult result = Iterables.getOnlyElement(parse("/*x*/ 1px /*x*/ solid red /*x*/\n/*x*/$"));
        assertThat(result.source.index()).isEqualTo(37);
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
    public void noErrorIfUnrecognizedTermNoOperator() {
        GenericParseResult result = parse("1px 1px/*x*/%").get(0);
        // no error
        assertThat(result.source.index()).isEqualTo(12);
    }

    @Test
    public void errorsIfUnrecognizedTermAfterOperator() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find another term");
        parse("1px 1px/*x*/,%");
    }

    @Test
    public void rawFunctionsBroadcastedBeforeGenericFunctions() {
        GenericParseResult result = parse("blah(BLAH)").get(0);
        ArrayList<Broadcastable> broadcasted = Lists.newArrayList(result.broadcasted);

        assertThat(broadcasted).hasSize(2);
        assertThat(broadcasted.get(0)).isInstanceOf(RawFunction.class);
        assertThat(broadcasted.get(1)).isInstanceOf(GenericFunctionValue.class);
    }
}
