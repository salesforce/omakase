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

package com.salesforce.omakase.parser.declaration;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.RawFunction;
import com.salesforce.omakase.ast.declaration.GenericFunctionValue;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.Operator;
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
        return ImmutableList.of("$$$", "    ", "\n\n\n", "","%");
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
                "     1px 1px",
                "  \n1px 1px",
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
                "/*xxxx   */   /* 1px */ 1px /*x*/ 1px 1px 1px",
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
            withExpectedResult("#ffcc11   ", 10),
            withExpectedResult("  round none ", 13),
            withExpectedResult("  round none ;", 13),
            withExpectedResult("  round none;", 12),
            withExpectedResult("  round none }", 13),
            withExpectedResult("  round none}", 12),
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
        assertThat(broadcasted).hasSize(14);

        assertThat(broadcasted.get(0)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(1)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(2)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(3)).isInstanceOf(Operator.class);
        assertThat(broadcasted.get(4)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(5)).isInstanceOf(RawFunction.class);
        assertThat(broadcasted.get(6)).isInstanceOf(GenericFunctionValue.class);
        assertThat(broadcasted.get(7)).isInstanceOf(Operator.class);
        assertThat(broadcasted.get(8)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(9)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(10)).isInstanceOf(Operator.class);
        assertThat(broadcasted.get(11)).isInstanceOf(NumericalValue.class);
        assertThat(broadcasted.get(12)).isInstanceOf(RawFunction.class);
        assertThat(broadcasted.get(13)).isInstanceOf(GenericFunctionValue.class);
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<ParseResult<Integer>> results = parseWithExpected(ImmutableList.of(
            withExpectedResult("0", 1),
            withExpectedResult("#ffcc11 ", 1),
            withExpectedResult("1px\n1px !important", 2),
            withExpectedResult("1px\t1px", 2),
            withExpectedResult("rotateX(80deg) rotateY(0deg) rotateZ(0deg)", 6), // RawFunction adds each
            withExpectedResult("-1px 1px 0 #222", 4),
            withExpectedResult("0 1px 3px rgba(0, 0, 0, 0.7),0 1px 0 rgba(0, 0, 0, 0.3)", 11), // RawFunction adds 1 each
            withExpectedResult("U+000-49F, U+27FF ,  U+29??,   U+1D400-1D7FF", 7),
            withExpectedResult("-1px 1px 0 #222", 4)));

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

    @Test
    public void missingSpaceBetweenTerms() {
        GenericParseResult result = parse("1px1px1px").get(0);
        assertThat(result.source.index()).isEqualTo(3);
    }

}
